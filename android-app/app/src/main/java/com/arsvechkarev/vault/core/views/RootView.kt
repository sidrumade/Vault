package com.arsvechkarev.vault.core.views

import android.content.Context
import android.view.WindowInsets
import android.widget.FrameLayout
import viewdsl.ViewDslConfiguration

import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class RootView(context: Context) : FrameLayout(context) {
  
  init {
    fitsSystemWindows = true
  }

override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
  val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)
  val systemWindowInsets = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())

  ViewDslConfiguration.setStatusBarHeight(systemWindowInsets.top)
  updatePadding(bottom = systemWindowInsets.bottom)

  return insets
}

}
