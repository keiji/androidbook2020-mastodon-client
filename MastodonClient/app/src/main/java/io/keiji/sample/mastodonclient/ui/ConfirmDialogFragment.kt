package io.keiji.sample.mastodonclient.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConfirmDialogFragment : DialogFragment() {

    companion object {
        val TAG = ConfirmDialogFragment::class.java.simpleName

        private const val KEY_TITLE = "key_title"
        private const val KEY_MESSAGE = "key_message"
        private const val KEY_LABEL_POSITIVE_BUTTON = "key_label_positive_button"
        private const val KEY_LABEL_NEGATIVE_BUTTON = "key_label_negative_button"

        @JvmStatic
        fun newInstance(
                title: String,
                message: String,
                positiveButtonLabel: String,
                negativeButtonLabel: String
        ): ConfirmDialogFragment {
            val args = Bundle().apply {
                putString(KEY_TITLE, title)
                putString(KEY_MESSAGE, message)
                putString(KEY_LABEL_POSITIVE_BUTTON, positiveButtonLabel)
                putString(KEY_LABEL_NEGATIVE_BUTTON, negativeButtonLabel)
            }
            return ConfirmDialogFragment().also {
                it.arguments = args
            }
        }
    }

    interface Callback {
        fun onClickPositiveButton()
        fun onClickNegativeButton()
    }

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callback) {
            callback = context
        }

        val fragment = targetFragment
        if (fragment is Callback) {
            callback = fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var title: String? = null
        var message: String? = null
        var positiveButtonLabel: String? = null
        var negativeButtonLabel: String? = null

        requireArguments().let {
            title = it.getString(KEY_TITLE)
            message = it.getString(KEY_MESSAGE)
            positiveButtonLabel = it.getString(KEY_LABEL_POSITIVE_BUTTON)
            negativeButtonLabel = it.getString(KEY_LABEL_NEGATIVE_BUTTON)
        }

        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        title?.also {
            alertDialogBuilder.setTitle(title)
        }

        message?.also {
            alertDialogBuilder.setMessage(it)
        }

        positiveButtonLabel?.also {
            alertDialogBuilder.setPositiveButton(it) { _, _ ->
                callback?.onClickPositiveButton()
            }
        }
        negativeButtonLabel?.also {
            alertDialogBuilder.setNegativeButton(it) { _, _ ->
                callback?.onClickNegativeButton()
            }
        }

        return alertDialogBuilder.create()
    }
}
