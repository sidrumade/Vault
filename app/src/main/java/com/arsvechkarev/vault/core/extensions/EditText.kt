package com.arsvechkarev.vault.core.extensions

import android.text.Editable
import android.text.TextWatcher

interface BaseTextWatcher : TextWatcher {
  
  override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
  override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
  override fun afterTextChanged(s: Editable) = onTextChange(s.toString())
  
  fun onTextChange(text: String)
}