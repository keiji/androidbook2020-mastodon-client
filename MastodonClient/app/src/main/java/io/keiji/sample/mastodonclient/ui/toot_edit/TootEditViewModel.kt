package io.keiji.sample.mastodonclient.ui.toot_edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope

class TootEditViewModel(
        private val instanceUrl: String,
        private val username: String,
        private val coroutineScope: CoroutineScope,
        application: Application
) : AndroidViewModel(application) {

    val status = MutableLiveData<String>()

}
