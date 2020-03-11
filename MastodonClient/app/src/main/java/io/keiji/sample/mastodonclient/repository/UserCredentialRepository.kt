package io.keiji.sample.mastodonclient.repository

import android.app.Application
import io.keiji.sample.mastodonclient.BuildConfig
import io.keiji.sample.mastodonclient.entity.UserCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserCredentialRepository(
    private val application: Application
) {

    suspend fun find(
        instanceUrl: String,
        username: String
    ): UserCredential? = withContext(Dispatchers.IO) {

        return@withContext null
    }

}