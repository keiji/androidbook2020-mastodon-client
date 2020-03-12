package io.keiji.sample.mastodonclient.repository

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MediaFileRepository(application: Application) {

    private val contentResolver = application.contentResolver

    private val saveDir = application.filesDir

    suspend fun readBitmap(
        mediaUri: Uri
    ): Bitmap = withContext(Dispatchers.IO) {
        @Suppress("DEPRECATION")
        return@withContext MediaStore.Images.Media.getBitmap(
            contentResolver,
            mediaUri
        )
    }

    suspend fun saveBitmap(
        bitmap: Bitmap
    ): File = withContext(Dispatchers.IO) {
        val tempFile = createTempFile(
            directory = saveDir,
            prefix = "media",
            suffix = ".jpg"
        )
        FileOutputStream(tempFile).use {
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, 100, it)
        }
        return@withContext tempFile
    }

}