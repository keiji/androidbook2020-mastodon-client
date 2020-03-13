package io.keiji.sample.mastodonclient.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostTootQueue(
    val instanceUrl: String,
    val username: String,
    val status: String
) {
    @PrimaryKey(autoGenerate=true)
    var id: Long = 0
}