package ru.appkode.base.ui.core.core.util

import android.widget.TextView
import androidx.core.view.isVisible

fun TextView.setVisibilityAndText(text: String?) {
    when (text.isNullOrBlank()) {
        true -> {
            isVisible = false
            this.text = ""
        }
        else -> {
            isVisible = true
            this.text = text
        }
    }
}
