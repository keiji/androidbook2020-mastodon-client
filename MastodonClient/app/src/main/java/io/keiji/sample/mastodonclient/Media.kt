package io.keiji.sample.mastodonclient

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(
    val id: String,
    val type: String,
    val url: String,
    @Json(name = "preview_url") val previewUrl: String
) : Parcelable