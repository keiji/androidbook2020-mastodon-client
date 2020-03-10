package io.keiji.sample.mastodonclient.ui.login

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.keiji.sample.mastodonclient.repository.TootRepository
import io.keiji.sample.mastodonclient.repository.UserCredentialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoginViewModel(
        private val instanceUrl: String,
        private val coroutineScope: CoroutineScope,
        application: Application
) : AndroidViewModel(application) {
}
