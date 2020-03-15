package io.keiji.sample.mastodonclient.ui.toot_list

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import io.keiji.sample.mastodonclient.entity.Account
import io.keiji.sample.mastodonclient.entity.Toot
import io.keiji.sample.mastodonclient.entity.UserCredential
import io.keiji.sample.mastodonclient.repository.AccountRepository
import io.keiji.sample.mastodonclient.repository.TootRepository
import io.keiji.sample.mastodonclient.repository.UserCredentialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

class TootListViewModel(
    private val instanceUrl: String,
    private val username: String,
    private val timelineType: TimelineType,
    private val coroutineScope: CoroutineScope,
    application: Application
) : AndroidViewModel(application), LifecycleObserver {

    private val userCredentialRepository = UserCredentialRepository(
        application
    )
    private lateinit var tootRepository: TootRepository
    private lateinit var accountRepository: AccountRepository

    private lateinit var userCredential: UserCredential

    val loginRequired = MutableLiveData<Boolean>()

    val isLoading = MutableLiveData<Boolean>()
    var hasNext = true

    val errorMessage = MutableLiveData<String>()
    val accountInfo = MutableLiveData<Account>()
    val tootList = MutableLiveData<ArrayList<Toot>>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        reloadUserCredential()
    }

    fun clear() {
        val tootListSnapshot = tootList.value ?: return
        tootListSnapshot.clear()
    }

    fun loadNext() {
        coroutineScope.launch {
            updateAccountInfo()

            isLoading.postValue(true)

            val tootListSnapshot = tootList.value ?: ArrayList()

            val maxId = tootListSnapshot.lastOrNull()?.id
            try {
                val tootListResponse = when (timelineType) {
                    TimelineType.PublicTimeline -> {
                        tootRepository.fetchPublicTimeline(
                            maxId = maxId,
                            onlyMedia = true
                        )
                    }
                    TimelineType.HomeTimeline -> {
                        tootRepository.fetchHomeTimeline(
                            maxId = maxId
                        )
                    }
                }

                val newTootList = ArrayList(tootListSnapshot)
                    .also {
                        it.addAll(tootListResponse)
                    }
                tootList.postValue(newTootList)
                hasNext = tootListResponse.isNotEmpty()
            } catch (e: HttpException) {
                when (e.code()) {
                    HttpURLConnection.HTTP_FORBIDDEN -> {
                        errorMessage.postValue("必要な権限がありません")
                    }
                }
            }  catch (e: IOException) {
                errorMessage.postValue(
                    "サーバーに接続できませんでした。${e.message}"
                )
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    private suspend fun updateAccountInfo() {
        try {
            val accountInfoSnapshot = accountInfo.value
                    ?: accountRepository.verifyAccountCredential()

            accountInfo.postValue(accountInfoSnapshot)
        } catch (e: HttpException) {
            when (e.code()) {
                HttpURLConnection.HTTP_FORBIDDEN -> {
                    errorMessage.postValue("必要な権限がありません")
                }
            }
        }  catch (e: IOException) {
            errorMessage.postValue(
                "サーバーに接続できませんでした。${e.message}"
            )
        }
    }

    fun delete(toot: Toot) {
        coroutineScope.launch {
            try {
                tootRepository.delete(toot.id)

                val tootListSnapshot = tootList.value ?: ArrayList()
                val newTootList = ArrayList(tootListSnapshot)
                    .also {
                        it.remove(toot)
                    }
                tootList.postValue(newTootList)
            }  catch (e: HttpException) {
                when (e.code()) {
                    HttpURLConnection.HTTP_FORBIDDEN -> {
                        errorMessage.postValue("必要な権限がありません")
                    }
                }
            }  catch (e: IOException) {
                errorMessage.postValue(
                    "サーバーに接続できませんでした。${e.message}"
                )
            }
        }
    }

    fun reloadUserCredential() {
        coroutineScope.launch {
            val credential = userCredentialRepository
                .find(instanceUrl, username)
            if (credential == null) {
                loginRequired.postValue(true)
                return@launch
            }

            tootRepository = TootRepository(credential)
            accountRepository = AccountRepository(credential)
            userCredential = credential

            clear()
            loadNext()
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        // Do nothing
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        reloadUserCredential()
    }
}