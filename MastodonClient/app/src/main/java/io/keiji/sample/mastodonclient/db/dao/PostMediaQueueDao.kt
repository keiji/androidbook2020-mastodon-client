package io.keiji.sample.mastodonclient.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.keiji.sample.mastodonclient.db.entity.PostMediaQueue

@Dao
interface PostMediaQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(data: PostMediaQueue): Long

    @Query("SELECT * FROM PostMediaQueue WHERE tootId = :tootId")
    suspend fun findByTootId(tootId: Long): List<PostMediaQueue>

    @Delete
    suspend fun delete(data: PostMediaQueue)
}