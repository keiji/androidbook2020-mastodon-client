package io.keiji.sample.mastodonclient

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.keiji.sample.mastodonclient.databinding.FragmentTootListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.atomic.AtomicBoolean

class TootListFragment : Fragment(R.layout.fragment_toot_list) {

    companion object {
        val TAG = TootListFragment::class.java.simpleName

        private const val API_BASE_URL = "https://androidbook2020.keiji.io"
    }

    private var binding: FragmentTootListBinding? = null

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val api = retrofit.create(MastodonApi::class.java)

    private lateinit var adapter: TootListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var isLoading = AtomicBoolean()
    private var hasNext = AtomicBoolean().apply { set(true) }

    private val loadNextScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (isLoading.get() || !hasNext.get()) {
                return
            }

            val visibleItemCount = recyclerView.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if ((totalItemCount - visibleItemCount) <= firstVisibleItemPosition) {
                loadNext()
            }
        }
    }

    private val tootList = ArrayList<Toot>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TootListAdapter(layoutInflater, tootList)
        layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false)
        val bindingData: FragmentTootListBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return

        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
            it.addOnScrollListener(loadNextScrollListener)
        }
        bindingData.swipeRefreshLayout.setOnRefreshListener {
            tootList.clear()
            loadNext()
        }

        loadNext()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    private suspend fun showProgress() = withContext(Dispatchers.Main) {
        binding?.swipeRefreshLayout?.isRefreshing = true
    }

    private suspend fun dismissProgress() = withContext(Dispatchers.Main) {
        binding?.swipeRefreshLayout?.isRefreshing = false
    }

    private fun loadNext() {
        lifecycleScope.launch {
            isLoading.set(true)
            showProgress()

            val tootListResponse = withContext(Dispatchers.IO) {
                api.fetchPublicTimeline(
                        maxId = tootList.lastOrNull()?.id,
                        onlyMedia = true
                )
            }
            Log.d(TAG, "fetchPublicTimeline")

            Thread.sleep(10 * 1000)

            tootList.addAll(tootListResponse.filter { !it.sensitive })
            Log.d(TAG, "addAll")

            reloadTootList()
            Log.d(TAG, "reloadTootList")

            isLoading.set(false)
            hasNext.set(tootListResponse.isNotEmpty())
            dismissProgress()
            Log.d(TAG, "dismissProgress")
        }
    }

    private suspend fun reloadTootList() = withContext(Dispatchers.Main) {
        adapter.notifyDataSetChanged()
    }

}