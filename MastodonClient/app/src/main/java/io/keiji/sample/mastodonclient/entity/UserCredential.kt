package io.keiji.sample.mastodonclient.entity

data class UserCredential(
    val instanceUrl: String,
    var username: String? = null,
    var accessToken: String? = null
)