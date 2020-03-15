package io.keiji.sample.mastodonclient.ui.toot_edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.keiji.sample.mastodonclient.BuildConfig
import io.keiji.sample.mastodonclient.R
import io.keiji.sample.mastodonclient.databinding.FragmentTootEditBinding
import io.keiji.sample.mastodonclient.service.PostTootService
import io.keiji.sample.mastodonclient.ui.login.LoginActivity
import timber.log.Timber

class TootEditFragment : Fragment(R.layout.fragment_toot_edit) {

    companion object {
        val TAG = TootEditFragment::class.java.simpleName

        private const val REQUEST_CODE_LOGIN = 0x01
        private const val REQUEST_CHOOSE_MEDIA = 0x02

        fun newInstance(): TootEditFragment {
            return TootEditFragment()
        }
    }

    private var binding: FragmentTootEditBinding? = null

    private val viewModel: TootEditViewModel by viewModels {
        TootEditViewModelFactory(
                BuildConfig.INSTANCE_URL,
                BuildConfig.USERNAME,
                lifecycleScope,
                requireContext()
        )
    }

    interface Callback {
        fun onPostComplete()

        fun onCloseEdit()
    }

    private var callback: Callback? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Timber.d("handleOnBackPressed")
            callback?.onCloseEdit()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setHasOptionsMenu(true)

        if (context is Callback) {
            callback = context
        }

        if (context is AppCompatActivity) {
            onBackPressedCallback.remove()
            context.onBackPressedDispatcher.addCallback(onBackPressedCallback)
        }
    }

    override fun onDetach() {
        super.onDetach()

        onBackPressedCallback.remove()
    }

    private lateinit var adapter: MediaPreviewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_clear_white_24)
        }

        val bindingData: FragmentTootEditBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return

        bindingData.lifecycleOwner = viewLifecycleOwner
        bindingData.viewModel = viewModel

        handleIntentExtras(requireActivity().intent)
        viewModel.onRestoreInstanceState(savedInstanceState)

        adapter = MediaPreviewAdapter(layoutInflater, lifecycleScope)
        layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        bindingData.mediaPreview.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
        }
        bindingData.addMedia.setOnClickListener {
            openMediaChooser()
        }

        viewModel.loginRequired.observe(viewLifecycleOwner, Observer {
            if (it) {
                launchLoginActivity()
            }
        })
        viewModel.mediaAttachments.observe(viewLifecycleOwner, Observer {
            adapter.mediaAttachments = it
        })
        viewModel.postComplete.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), "投稿キューに追加しました", Toast.LENGTH_LONG).show()

            callback?.onPostComplete()
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        viewModel.onSaveInstanceState(outState)
    }

    private fun handleIntentExtras(intent: Intent) {
        val extras = intent.extras ?: return
        val mimeType = intent.type
        when {
            mimeType == null -> {
                // nothing to do
            }
            mimeType == "text/plain" -> {
                val text = extras.getString(Intent.EXTRA_TEXT)
                viewModel.status.postValue(text)
            }
            mimeType.startsWith("image/") -> {
                extras.getParcelable<Uri>(Intent.EXTRA_STREAM)?.also {
                    viewModel.addMedia(it)
                }
            }
        }
    }

    private fun launchPostService() {
        val intent = Intent(requireContext(), PostTootService::class.java)
        requireActivity().startService(intent)
    }

    private fun openMediaChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CHOOSE_MEDIA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data?.data
        if (requestCode == REQUEST_CHOOSE_MEDIA
            && resultCode == Activity.RESULT_OK
            && uri != null) {
            viewModel.addMedia(uri)
        }
    }

    private fun launchLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_LOGIN)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.toot_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                callback?.onCloseEdit()
                true
            }
            R.id.menu_post -> {
                viewModel.addTootQueue()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }
}