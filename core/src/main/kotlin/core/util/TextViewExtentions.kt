package ru.appkode.base.ui.core.core.util

import android.widget.TextView
import androidx.core.view.isVisible

fun TextView.setVisibilityAndText(text: String?) {
    isVisible = !text.isNullOrBlank()
    this.text = text ?: ""
}
