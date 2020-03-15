package io.keiji.sample.mastodonclient

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.keiji.sample.mastodonclient.databinding.FragmentTootDetailBinding

class TootDetailFragment : Fragment(R.layout.fragment_toot_detail) {

    companion object {
        val TAG = TootDetailFragment::class.java.simpleName

        private const val BUNDLE_KEY_TOOT = "bundle_key_toot"

        @JvmStatic
        fun newInstance(toot: Toot): TootDetailFragment {
            val args = Bundle().apply {
                putParcelable(BUNDLE_KEY_TOOT, toot)
            }
            return TootDetailFragment().apply {
                arguments = args
            }
        }
    }

    private var toot: Toot? = null

    private var binding: FragmentTootDetailBinding? = null

    private val viewModel: TootDetailViewModel by viewModels {
        TootDetailViewModelFactory(
            toot,
            lifecycleScope,
            requireContext()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().also {
            toot = it.getParcelable(BUNDLE_KEY_TOOT)
        }
    }

    private lateinit var adapter : MediaListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bindingData: FragmentTootDetailBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return

        if (toot == null) {
            showTootNotFound()
            return
        }

        bindingData.recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        )
        bindingData.recyclerView.adapter = MediaListAdapter(layoutInflater).also {
            adapter = it
        }

        viewModel.toot.observe(viewLifecycleOwner, Observer {
            bindingData.toot = it
            adapter.mediaList = it.mediaAttachments
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    private fun showTootNotFound() {
        Toast.makeText(requireContext(), "Toot not found", Toast.LENGTH_LONG).show()
    }
}