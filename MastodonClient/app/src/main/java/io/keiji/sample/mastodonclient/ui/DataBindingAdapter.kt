package io.keiji.sample.mastodonclient.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import io.keiji.sample.mastodonclient.entity.LocalMedia
import io.keiji.sample.mastodonclient.entity.Media

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