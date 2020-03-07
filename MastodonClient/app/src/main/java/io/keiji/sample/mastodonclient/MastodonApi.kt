package io.keiji.sample.mastodonclient

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface MastodonApi {

    @GET("api/v1/timelines/public")
    fun fetchPublicTimeline(
    ): Call<ResponseBody>
}