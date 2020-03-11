package io.keiji.sample.mastodonclient.ui.toot_edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.keiji.sample.mastodonclient.repository.TootRepository
import io.keiji.sample.mastodonclient.repository.UserCredentialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.HttpURLConnection

class TootEditViewModel(
        private val instanceUrl: String,
        private val username: String,
        private val coroutineScope: CoroutineScope,
        application: Application
) : AndroidViewModel(application) {

    private val userCredentialRepository = UserCredentialRepository(
        application
    )

    val status = MutableLiveData<String>()

    val loginRequired = MutableLiveData<Boolean>()

    val postComplete = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun postToot() {
        val statusSnapshot = status.value ?: return
        if (statusSnapshot.isBlank()) {
            errorMessage.postValue("投稿内容がありません")
            return
        }

        coroutineScope.launch {
            val credential = userCredentialRepository.find(instanceUrl, username)
            if (credential == null) {
                loginRequired.postValue(true)
                return@launch
            }

            val tootRepository = TootRepository(credential)
            try {
                tootRepository.postToot(
                    statusSnapshot
                )
                postComplete.postValue(true)
            } catch (e: HttpException) {
                when (e.code()) {
                    HttpURLConnection.HTTP_FORBIDDEN -> {
                        errorMessage.postValue("必要な権限がありません")
                    }
                }
            }
        }
    }
}
