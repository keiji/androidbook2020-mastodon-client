package io.keiji.sample.mastodonclient.ui.toot_edit

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope

class TootEditViewModelFactory(
        private val instanceUrl: String,
        private val username: String,
        private val coroutineScope: CoroutineScope,
        private val context: Context
        ) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TootEditViewModel(
                instanceUrl,
                username,
                coroutineScope,
                context.applicationContext as Application
        ) as T
    }
}
