package io.keiji.sample.mastodonclient.ui.toot_edit

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.keiji.sample.mastodonclient.entity.LocalMedia
import io.keiji.sample.mastodonclient.repository.MediaFileRepository
import io.keiji.sample.mastodonclient.repository.PostTootQueueRepository
import io.keiji.sample.mastodonclient.repository.TootRepository
import io.keiji.sample.mastodonclient.repository.UserCredentialRepository
import io.keiji.sample.mastodonclient.worker.PostTootWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.xml.transform.OutputKeys.MEDIA_TYPE

class TootEditViewModel(
        private val instanceUrl: String,
        private val username: String,
        private val coroutineScope: CoroutineScope,
        application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val KEY_STATUS = "status"
        private const val KEY_MEDIA_ATTACHMENTS = "media_attachments"
    }

    private val userCredentialRepository = UserCredentialRepository(
        application
    )
    private val postTootQueueRepository = PostTootQueueRepository(application)
    private val mediaFileRepository = MediaFileRepository(application)

    private val workManager = WorkManager.getInstance(application)

    val status = MutableLiveData<String>()

    val loginRequired = MutableLiveData<Boolean>()

    val postComplete = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun addTootQueue() {
        val statusSnapshot = status.value ?: return
        if (statusSnapshot.isBlank()) {
            errorMessage.postValue("投稿内容がありません")
            return
        }

        coroutineScope.launch {
            postTootQueueRepository.addQueue(
                instanceUrl,
                username,
                statusSnapshot,
                mediaAttachments.value
            )

            enqueueWorker()

            postComplete.postValue(true)
        }
    }

    private fun enqueueWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val postWorkRequest = OneTimeWorkRequestBuilder<PostTootWorker>()
            .setConstraints(constraints)
            .build()
        workManager.enqueue(postWorkRequest)
    }

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
                val uploadedMediaIds = mediaAttachments.value?.map {
                    tootRepository.postMedia(it.file, it.mediaType)
                }?.map { it.id }

                tootRepository.postToot(
                    statusSnapshot,
                    uploadedMediaIds
                )
                postComplete.postValue(true)
            } catch (e: HttpException) {
                when (e.code()) {
                    HttpURLConnection.HTTP_FORBIDDEN -> {
                        errorMessage.postValue("必要な権限がありません")
                    }
                }
            } catch (e: IOException) {
                errorMessage.postValue(
                    "サーバーに接続できませんでした。${e.message}"
                )
            }
        }
    }

    val mediaAttachments = MutableLiveData<ArrayList<LocalMedia>>()

    fun addMedia(mediaUri: Uri) {
        coroutineScope.launch {
            try {
                val bitmap = mediaFileRepository.readBitmap(mediaUri)
                val tempFile = mediaFileRepository.saveBitmap(bitmap)

                val newMediaAttachments = ArrayList<LocalMedia>()
                mediaAttachments.value?.also {
                    newMediaAttachments.addAll(it)
                }
                newMediaAttachments.add(LocalMedia(tempFile, MEDIA_TYPE))
                mediaAttachments.postValue(newMediaAttachments)

            } catch (e: IOException) {
                handleMediaException(mediaUri, e)
            }
        }
    }

    private fun handleMediaException(mediaUri: Uri, e: IOException) {
        errorMessage.postValue("メディアを読み込めません ${e.message} ${mediaUri}")
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_STATUS, status.value)
        outState.putParcelableArrayList(
            KEY_MEDIA_ATTACHMENTS,
            mediaAttachments.value
        )
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        status.postValue(savedInstanceState.getString(KEY_STATUS))
        val mediaList: ArrayList<LocalMedia> = savedInstanceState
            .getParcelableArrayList(KEY_MEDIA_ATTACHMENTS) ?: ArrayList()
        mediaAttachments.postValue(mediaList)
    }
}
