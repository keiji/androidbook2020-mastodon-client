package io.keiji.sample.mastodonclient

import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("spannedContent")
fun TextView.setSpannedString(content: String) {
    text = HtmlCompat.fromHtml(
        content,
        HtmlCompat.FROM_HTML_MODE_COMPACT
    )
}