package com.arsvechkarev.vault.viewdsl

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.EditText

interface BaseTextWatcher : TextWatcher {
  
  override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
  override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
  override fun afterTextChanged(s: Editable) = onTextChange(s.toString())
  
  fun onTextChange(text: String)
}

fun EditText.setMaxLength(max: Int) {
  val filterArray = arrayOfNulls<InputFilter>(1)
  filterArray[0] = InputFilter.LengthFilter(max)
  filters = filterArray
}

fun EditText.onTextChanged(block: (text: String) -> Unit) {
  addTextChangedListener(object : BaseTextWatcher {
    override fun onTextChange(text: String) {
      block(text)
    }
  })
}

fun EditText.onSubmit(func: () -> Unit) {
  imeOptions = IME_ACTION_DONE
  setOnEditorActionListener { _, actionId, _ ->
    if (actionId == IME_ACTION_DONE) {
      func()
      return@setOnEditorActionListener true
    }
    return@setOnEditorActionListener false
  }
}