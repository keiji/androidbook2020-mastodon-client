package io.keiji.sample.mastodonclient.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import io.keiji.sample.mastodonclient.db.entity.PostMediaQueue
import io.keiji.sample.mastodonclient.db.entity.PostTootQueue
import io.keiji.sample.mastodonclient.repository.PostTootQueueRepository
import io.keiji.sample.mastodonclient.repository.TootRepository
import io.keiji.sample.mastodonclient.repository.UserCredentialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection

class PostTootService : IntentService(PostTootService::class.java.simpleName) {

    companion object {
        private val TAG = PostTootService::class.java.simpleName
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onHandleIntent(intent: Intent?) {
        val postTootQueueRepository = PostTootQueueRepository(application)
        val userCredentialRepository = UserCredentialRepository(application)

        coroutineScope.launch {
            postTootQueueRepository
                .allTootQueues()
                .forEach { queue ->
                    try {
                        postToot(
                            queue,
                            postTootQueueRepository,
                            userCredentialRepository
                        )
                    } catch (e: HttpException) {
                        handleException(e)
                    } catch (e: IOException) {
                        handleException(e)
                    }
                }
        }
    }

    private suspend fun postToot(
        queue: PostTootQueue,
        postTootQueueRepository: PostTootQueueRepository,
        userCredentialRepository: UserCredentialRepository
    ) {
        val userCredential = userCredentialRepository.find(
            queue.instanceUrl,
            queue.username
        ) ?: return

        val tootRepository = TootRepository(userCredential)

        val medias = postTootQueueRepository
            .findMediasByTootId(queue.id)

        val mediaIds = uploadMedias(
            medias,
            tootRepository,
            postTootQueueRepository
        )

        tootRepository.postToot(
            queue.status,
            mediaIds
        )

        postTootQueueRepository.delete(queue)
    }

    private suspend fun uploadMedias(
        medias: List<PostMediaQueue>,
        tootRepository: TootRepository,
        postTootQueueRepository: PostTootQueueRepository
    ): List<String> {
        return medias.mapNotNull { mediaQueue ->
            if (mediaQueue.mediaId != null) {
                return@mapNotNull mediaQueue.mediaId
            }

            val file = File(mediaQueue.file)
            val media = tootRepository.postMedia(
                file,
                mediaQueue.mediaType
            )
            mediaQueue.mediaId = media.id
            postTootQueueRepository.upsert(mediaQueue)
            file.deleteOnExit()

            return@mapNotNull mediaQueue.mediaId
        }
    }

    private fun handleException(e: HttpException) {
        val message = when (e.code()) {
            HttpURLConnection.HTTP_FORBIDDEN -> "権限がありません: ${e.message}"
            else -> "不明なエラーです ${e.message}"
        }
        Log.e(TAG, message)
    }

    private fun handleException(e: IOException) {
        Log.e(TAG, "IOException", e)
    }
}