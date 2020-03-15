package io.keiji.sample.mastodonclient.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import io.keiji.sample.mastodonclient.entity.LocalMedia
import io.keiji.sample.mastodonclient.entity.Media
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@BindingAdapter("media")
fun ImageView.setMedia(media: Media?) {
    if (media == null) {
        setImageDrawable(null)
        return
    }
    Glide.with(this)
            .load(media.url)
            .into(this)
}

@BindingAdapter("localMedia")
fun ImageView.setLocalMedia(localMedia: LocalMedia?) {
    if (localMedia == null) {
        setImageDrawable(null)
        return
    }
    Glide.with(this)
        .load(localMedia.file)
        .into(this)
}

@BindingAdapter("spannedContent")
fun TextView.setSpannedString(content: String) {
    text = HtmlCompat.fromHtml(
        content,
        HtmlCompat.FROM_HTML_MODE_COMPACT
    )
}

private var iso8601Formatter: DateFormat = SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private var dateFormatter: DateFormat = SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss",
    Locale.getDefault()
).apply {
    timeZone = TimeZone.getDefault()
}

@BindingAdapter("dateText")
fun TextView.setDateString(iso8601DateText: String) {
    val date = iso8601Formatter.parse(iso8601DateText) ?: return
    text = dateFormatter.format(date)
}
