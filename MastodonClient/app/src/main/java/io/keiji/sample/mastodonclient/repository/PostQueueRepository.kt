package io.keiji.sample.mastodonclient.repository

import android.app.Application
import io.keiji.sample.mastodonclient.MyApplication
import io.keiji.sample.mastodonclient.db.entity.PostMediaQueue
import io.keiji.sample.mastodonclient.db.entity.PostTootQueue
import io.keiji.sample.mastodonclient.entity.LocalMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostTootQueueRepository(application: Application) {

    private val appDatabase = MyApplication.getAppDatabase(application)

    suspend fun addQueue(
        instanceUrl: String,
        username: String,
        status: String,
        mediaAttachments: ArrayList<LocalMedia>?
    ) = withContext(Dispatchers.IO) {
        val tootQueue = PostTootQueue(
            instanceUrl,
            username,
            status
        )

        val tootId = appDatabase
            .postTootQueue()
            .upsert(tootQueue)

        val postMediaQueueDao = appDatabase.postMediaQueue()
        mediaAttachments?.forEach {
            val mediaQueue = PostMediaQueue(
                tootId,
                it.file.absolutePath,
                it.mediaType
            )
            postMediaQueueDao.upsert(mediaQueue)
        }

        return@withContext tootQueue
    }

    suspend fun delete(
        postTootQueue: PostTootQueue
    ) = withContext(Dispatchers.IO) {
        appDatabase
            .postTootQueue()
            .delete(postTootQueue)
    }

    suspend fun upsert(
        postMediaQueue: PostMediaQueue
    ) = withContext(Dispatchers.IO) {
        appDatabase
            .postMediaQueue()
            .upsert(postMediaQueue)
    }

    suspend fun allTootQueues(
    ): List<PostTootQueue> = withContext(Dispatchers.IO) {
        return@withContext appDatabase
            .postTootQueue()
            .all()
    }

    suspend fun findMediasByTootId(
        tootId: Long
    ): List<PostMediaQueue> = withContext(Dispatchers.IO) {
        return@withContext appDatabase
            .postMediaQueue()
            .findByTootId(tootId)
    }
}
