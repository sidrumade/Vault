package com.arsvechkarev.vault.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import com.arsvechkarev.vault.core.extensions.TextPaint
import com.arsvechkarev.vault.core.extensions.getTextHeight
import com.arsvechkarev.vault.core.extensions.i
import com.arsvechkarev.vault.viewbuilding.Colors
import com.arsvechkarev.vault.viewbuilding.Dimens
import com.arsvechkarev.vault.viewbuilding.Fonts
import com.arsvechkarev.vault.viewdsl.Ints.dp
import com.arsvechkarev.vault.viewdsl.retrieveDrawable
import com.arsvechkarev.vault.viewdsl.rippleBackground

class TextWithImageView(
  context: Context,
  iconRes: Int,
  textSize: Float,
  private var text: String
) : View(context) {
  
  private var icon = context.retrieveDrawable(iconRes)
  private val textPaint = TextPaint(textSize, font = Fonts.SegoeUiBold)
  
  private val iconSize = 35.dp
  
  init {
    rippleBackground(Colors.Ripple, Colors.Transparent, Dimens.DefaultCornerRadius)
    val padding = 4.dp
    setPadding(padding, padding, padding, padding)
  }
  
  fun setText(textRes: Int) {
    text = context.getString(textRes)
    invalidate()
  }
  
  fun setImage(drawableRes: Int) {
    icon = context.retrieveDrawable(drawableRes)
    icon.colorFilter = PorterDuffColorFilter(Colors.Icon, PorterDuff.Mode.SRC_ATOP)
    invalidate()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val textWidth = textPaint.measureText(text).i
    val textHeight = (textPaint.getTextHeight() * 2.5f).i
    val width = maxOf(iconSize, textWidth) + paddingStart + paddingEnd
    val height = iconSize + textHeight + paddingTop + paddingBottom
    setMeasuredDimension(width, height)
  }
  
  override fun onDraw(canvas: Canvas) {
    if (icon.bounds.width() == 0) {
      icon.setBounds(
        width / 2 - iconSize / 2, paddingTop,
        width / 2 + iconSize / 2, paddingTop + iconSize
      )
    }
    icon.draw(canvas)
    val y = height - paddingBottom - textPaint.getTextHeight(text) / 2f
    canvas.drawText(text, width / 2f, y, textPaint)
  }
}