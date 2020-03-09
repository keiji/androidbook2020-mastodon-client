package io.keiji.sample.mastodonclient

data class UserCredential(
    val instanceUrl: String,
    var username: String? = null,
    var accessToken: String? = null
)