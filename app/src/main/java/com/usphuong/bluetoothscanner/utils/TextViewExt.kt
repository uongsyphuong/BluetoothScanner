package com.usphuong.bluetoothscanner.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.TextView
import java.util.regex.Matcher

fun TextView.setLinkClickEvent(listener: (String) -> Unit) {
    val text = this.text.toString()
    val matcher: Matcher = Patterns.WEB_URL.matcher(this.text)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        val f = SpannableString(
            this.text
        )
        val span = object : ClickableSpan() {
            override fun onClick(p0: View) {
                listener.invoke(text.substring(start, end))
            }
        }
        f.setSpan(
            span, start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        this.text = f
    }
    this.linksClickable = true
    this.movementMethod = LinkMovementMethod.getInstance()
    this.isFocusable = false
}
