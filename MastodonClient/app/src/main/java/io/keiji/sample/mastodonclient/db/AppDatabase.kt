package io.keiji.sample.mastodonclient.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.keiji.sample.mastodonclient.db.dao.PostMediaQueueDao
import io.keiji.sample.mastodonclient.db.dao.PostTootQueueDao
import io.keiji.sample.mastodonclient.db.entity.PostMediaQueue
import io.keiji.sample.mastodonclient.db.entity.PostTootQueue

@Database(
    version = 1,
    entities = [
        PostTootQueue::class,
        PostMediaQueue::class
    ])
abstract class AppDatabase : RoomDatabase() {

    abstract fun postTootQueue(): PostTootQueueDao

    abstract fun postMediaQueue(): PostMediaQueueDao
}