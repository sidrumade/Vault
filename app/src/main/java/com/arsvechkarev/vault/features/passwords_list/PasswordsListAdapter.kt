package com.arsvechkarev.vault.features.passwords_list

import android.view.Gravity.CENTER
import android.widget.ImageView
import android.widget.TextView
import com.arsvechkarev.vault.core.model.ServiceInfo
import com.arsvechkarev.vault.recycler.CallbackType.ALWAYS_FALSE
import com.arsvechkarev.vault.recycler.ListAdapter
import com.arsvechkarev.vault.recycler.delegate
import com.arsvechkarev.vault.viewbuilding.Colors
import com.arsvechkarev.vault.viewbuilding.Dimens.ItemServiceInfoImageSize
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginDefault
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginSmall
import com.arsvechkarev.vault.viewbuilding.Styles.BoldTextView
import com.arsvechkarev.vault.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.vault.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.vault.viewdsl.childViewAs
import com.arsvechkarev.vault.viewdsl.image
import com.arsvechkarev.vault.viewdsl.layoutGravity
import com.arsvechkarev.vault.viewdsl.margins
import com.arsvechkarev.vault.viewdsl.onClick
import com.arsvechkarev.vault.viewdsl.padding
import com.arsvechkarev.vault.viewdsl.rippleBackground
import com.arsvechkarev.vault.viewdsl.tag
import com.arsvechkarev.vault.viewdsl.text
import com.arsvechkarev.vault.views.drawables.LetterInCircleDrawable

class PasswordsListAdapter(
  private val onItemClick: (ServiceInfo) -> Unit
) : ListAdapter(callbackType = ALWAYS_FALSE) {
  
  init {
    addDelegates(
      delegate<ServiceInfo> {
        buildView {
          RootHorizontalLayout(MatchParent, WrapContent) {
            rippleBackground(Colors.Ripple)
            padding(MarginSmall)
            ImageView(ItemServiceInfoImageSize, ItemServiceInfoImageSize) {
              tag(ItemServiceInfoImage)
              layoutGravity(CENTER)
              margins(MarginDefault)
            }
            TextView(WrapContent, WrapContent, style = BoldTextView) {
              tag(ItemServiceInfoTextServiceName)
              margins(MarginDefault)
              layoutGravity(CENTER)
            }
          }
        }
        onInitViewHolder {
          itemView.onClick { onItemClick(item) }
        }
        onBind {
          val letter = item.name[0].toString()
          val imageLetter = itemView.childViewAs<ImageView>(ItemServiceInfoImage)
          if (imageLetter.drawable == null) {
            imageLetter.image(LetterInCircleDrawable(letter))
          } else {
            (imageLetter.drawable as LetterInCircleDrawable).setLetter(letter)
          }
          itemView.childViewAs<TextView>(ItemServiceInfoTextServiceName).text(item.name)
        }
      }
    )
  }
  
  private companion object {
    
    const val ItemServiceInfoImage = "ItemServiceInfoImage"
    const val ItemServiceInfoTextServiceName = "ItemServiceInfoTextServiceName"
  }
}