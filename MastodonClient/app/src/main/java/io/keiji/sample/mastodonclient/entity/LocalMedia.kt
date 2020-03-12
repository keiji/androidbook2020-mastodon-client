package io.keiji.sample.mastodonclient.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class LocalMedia(
        val file: File,
        val mediaType: String
) : Parcelable {
}