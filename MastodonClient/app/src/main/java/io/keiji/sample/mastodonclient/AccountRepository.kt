package io.keiji.sample.mastodonclient

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AccountRepository(
    private val userCredential: UserCredential
) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(userCredential.instanceUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val api = retrofit.create(MastodonApi::class.java)

    suspend fun verifyAccountCredential(
    ): Account = withContext(Dispatchers.IO) {
        return@withContext api.verifyAccountCredential(
            "Bearer ${userCredential.accessToken}"
        )
    }

}