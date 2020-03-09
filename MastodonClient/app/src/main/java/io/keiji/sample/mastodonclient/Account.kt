package io.keiji.sample.mastodonclient

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    val id: String,
    val username: String,
    @Json(name = "display_name") val displayName: String,
    val url: String
) : Parcelable