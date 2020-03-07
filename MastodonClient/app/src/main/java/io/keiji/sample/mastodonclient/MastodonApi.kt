package io.keiji.sample.mastodonclient

import retrofit2.http.GET
import retrofit2.http.Query

interface MastodonApi {

    @GET("api/v1/timelines/public")
    suspend fun fetchPublicTimeline(
        @Query("max_id") maxId: String? = null,
        @Query("only_media") onlyMedia: Boolean = false
    ): List<Toot>
}