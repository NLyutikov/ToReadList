package ru.appkode.base.ui.core.core.util

import android.widget.EditText

fun EditText.setTextSafe(text: String) {
  if (!isFocused) setTextForce(text)
}

fun EditText.setTextForce(text: String) {
  if (text != this.text.toString()) setTextSafeInternal(text)
}

private fun EditText.setTextSafeInternal(text: String) {
  setText(text)
  setSelection(text.length)
}
