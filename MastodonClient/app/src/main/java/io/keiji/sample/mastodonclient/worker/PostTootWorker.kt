package io.keiji.sample.mastodonclient.worker

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.keiji.sample.mastodonclient.db.entity.PostMediaQueue
import io.keiji.sample.mastodonclient.repository.PostTootQueueRepository
import io.keiji.sample.mastodonclient.repository.TootRepository
import io.keiji.sample.mastodonclient.repository.UserCredentialRepository
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection

class PostTootWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val postTootQueueRepository = PostTootQueueRepository(
        applicationContext as Application
    )
    private val userCredentialRepository = UserCredentialRepository(
        applicationContext as Application
    )

    override suspend fun doWork(): Result {

        return postQueuedToots()
    }

    private suspend fun postQueuedToots(): Result {
        return try {
            postTootQueueRepository
                .allTootQueues()
                .forEach { queue ->
                    val userCredential = userCredentialRepository
                        .find(queue.instanceUrl, queue.username)
                    userCredential ?: return@forEach

                    val tootRepository = TootRepository(userCredential)

                    val medias = postTootQueueRepository
                        .findMediasByTootId(queue.id)

                    val mediaIds = uploadMedias(
                        medias,
                        tootRepository
                    )

                    tootRepository.postToot(
                        queue.status,
                        mediaIds
                    )

                    postTootQueueRepository.delete(queue)
                }
            Result.success()
        } catch (e: HttpException) {
            handleException(e)
        } catch (e: IOException) {
            handleException(e)
        }
    }

    private suspend fun uploadMedias(
        medias: List<PostMediaQueue>,
        tootRepository: TootRepository
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

    private fun handleException(e: HttpException): Result {
        val (message, result) = when (e.code()) {
            HttpURLConnection.HTTP_FORBIDDEN -> {
                Pair("権限がありません: ${e.message}", Result.failure())
            }
            else -> {
                Pair("不明なエラーです ${e.message}", Result.retry())
            }
        }
        Timber.e(message)
        return result
    }

    private fun handleException(e: IOException): Result {
        Timber.e(e)
        return Result.retry()
    }
}