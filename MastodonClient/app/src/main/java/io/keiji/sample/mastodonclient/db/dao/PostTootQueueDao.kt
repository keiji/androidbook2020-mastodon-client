package io.keiji.sample.mastodonclient.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.keiji.sample.mastodonclient.db.entity.PostTootQueue

@Dao
interface PostTootQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(data: PostTootQueue): Long

    @Query("SELECT * FROM PostTootQueue")
    suspend fun all(): List<PostTootQueue>

    @Delete
    suspend fun delete(data: PostTootQueue)
}