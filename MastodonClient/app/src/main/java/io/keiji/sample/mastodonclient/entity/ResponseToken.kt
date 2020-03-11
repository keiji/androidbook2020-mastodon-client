package io.keiji.sample.mastodonclient.entity

import com.squareup.moshi.Json

data class ResponseToken(
        @Json(name = "access_token") val accessToken: String,
        @Json(name = "token_type") val tokenType: String,
        val scope: String,
        @Json(name = "created_at") val createdAt: Long
) {
}