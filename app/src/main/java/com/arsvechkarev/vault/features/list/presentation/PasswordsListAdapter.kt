package com.arsvechkarev.vault.features.list.presentation

import android.view.Gravity
import com.arsvechkarev.vault.core.model.PasswordInfo
import com.arsvechkarev.vault.recycler.ListAdapter
import com.arsvechkarev.vault.recycler.delegate
import com.arsvechkarev.vault.viewbuilding.Colors
import com.arsvechkarev.vault.viewbuilding.Dimens.ImageServiceItemSize
import com.arsvechkarev.vault.viewbuilding.Styles
import com.arsvechkarev.vault.viewdsl.Ints.dp
import com.arsvechkarev.vault.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.vault.viewdsl.layoutGravity
import com.arsvechkarev.vault.viewdsl.margins
import com.arsvechkarev.vault.viewdsl.onClick
import com.arsvechkarev.vault.viewdsl.rippleBackground
import com.arsvechkarev.vault.viewdsl.tag

class PasswordsListAdapter(
  private val onItemClick: (PasswordInfo) -> Unit
) : ListAdapter() {
  
  init {
    addDelegates(
      delegate<PasswordInfo> {
        buildView {
          RootHorizontalLayout {
            rippleBackground(Colors.Ripple)
            ImageView(ImageServiceItemSize, ImageServiceItemSize) {
              tag("ImagePasswordItem")
              layoutGravity(Gravity.CENTER)
              margins(16.dp)
            }
            TextView(WrapContent, WrapContent, style = Styles.BoldTextView) {
              tag("TextServiceItemName")
              layoutGravity(Gravity.CENTER)
            }
          }
        }
        onInitViewHolder {
          itemView.onClick { onItemClick(item) }
        }
      }
    )
  }
}