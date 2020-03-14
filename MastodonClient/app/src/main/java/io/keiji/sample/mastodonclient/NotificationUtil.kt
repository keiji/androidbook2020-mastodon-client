package io.keiji.sample.mastodonclient

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
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
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return
    }

    val channel = NotificationChannel(
        channelId,
        name,
        importance
    )
    val nm = NotificationManagerCompat.from(context)
    nm.createNotificationChannel(channel)
}

private const val NOTIFY_ID_GENERAL = 0
private const val NOTIFY_ID_ERROR = -1

fun showNotification(context: Context, message: String) {
    val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID_GENERAL
        )
        .setContentTitle("投稿を完了しました")
        .setContentText(message)
        .setSmallIcon(R.drawable.baseline_done_black_18)
        .build()
    val nm = NotificationManagerCompat.from(context)
    nm.notify(NOTIFY_ID_GENERAL, notification)
}

fun showErrorNotification(context: Context, message: String) {
    val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID_ERROR
        )
        .setContentTitle("投稿でエラーが発生しました")
        .setContentText(message)
        .setSmallIcon(R.drawable.baseline_report_problem_black_18)
        .build()
    val nm = NotificationManagerCompat.from(context)
    nm.notify(NOTIFY_ID_ERROR, notification)
}
