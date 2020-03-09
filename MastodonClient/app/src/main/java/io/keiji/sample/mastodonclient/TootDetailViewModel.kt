package io.keiji.sample.mastodonclient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope

class TootDetailViewModel(
    private val tootData: Toot?,
    private val coroutineScope: CoroutineScope,
    application: Application
) : AndroidViewModel(application) {

    val toot = MutableLiveData<Toot>().also {
        it.value = tootData
    }
}