package io.keiji.sample.mastodonclient

import android.app.NotificationChannel
import android.content.Context
import androidx.core.app.NotificationManagerCompat

private const val CHANNEL_ID_GENERAL = "general"
private const val CHANNEL_ID_ERROR = "error"

fun registerNotificationChannelGeneral(
    applicationContext: Context
) {
    registerNotificationChannel(
        CHANNEL_ID_GENERAL,
        "一般",
        NotificationManagerCompat.IMPORTANCE_DEFAULT,
        applicationContext
    )
}

fun registerNotificationChannelError(
    applicationContext: Context
) {
    registerNotificationChannel(
        CHANNEL_ID_ERROR,
        "投稿エラー",
        NotificationManagerCompat.IMPORTANCE_HIGH,
        applicationContext
    )
}

private fun registerNotificationChannel(
    channelId: String,
    name: String,
    importance: Int,
    context: Context
) {
    val channel = NotificationChannel(
        channelId,
        name,
        importance
    )
    val nm = NotificationManagerCompat.from(context)
    nm.createNotificationChannel(channel)
}