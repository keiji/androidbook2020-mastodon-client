package io.keiji.sample.mastodonclient.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = PostTootQueue::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("tootId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PostMediaQueue(
    val tootId: Long,
    val file: String,
    val mediaType: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var mediaId: String? = null
}