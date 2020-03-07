package io.keiji.sample.mastodonclient

import com.squareup.moshi.Json

data class Account(
    val id: String,
    val username: String,
    @Json(name = "display_name") val displayName: String,
    val url: String
)