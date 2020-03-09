package io.keiji.sample.mastodonclient

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope

class TootListViewModelFactory(
        private val instanceUrl: String,
        private val username: String,
        private val coroutineScope: CoroutineScope,
        private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TootListViewModel(
                instanceUrl,
                username,
                coroutineScope,
                context.applicationContext as Application
        ) as T
    }
}