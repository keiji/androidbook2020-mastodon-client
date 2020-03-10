package io.keiji.sample.mastodonclient.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope

class LoginViewModelFactory(
        private val instanceUrl: String,
        private val coroutineScope: CoroutineScope,
        private val context: Context
        ) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(
                instanceUrl,
                coroutineScope,
                context.applicationContext as Application
        ) as T
    }
}
