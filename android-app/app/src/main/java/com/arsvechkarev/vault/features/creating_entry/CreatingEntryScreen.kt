package com.arsvechkarev.vault.features.creating_entry

import android.content.Context
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.widget.ImageView.ScaleType.FIT_XY
import androidx.annotation.StringRes
import com.arsvechkarev.vault.R
import com.arsvechkarev.vault.core.di.appComponent
import com.arsvechkarev.vault.core.mvi.ext.subscribe
import com.arsvechkarev.vault.core.mvi.ext.viewModelStore
import com.arsvechkarev.vault.core.setServiceIcon
import com.arsvechkarev.vault.features.creating_entry.CreatingEntryUiEvent.OnBackButtonClicked
import com.arsvechkarev.vault.features.creating_entry.CreatingEntryUiEvent.OnContinueClicked
import com.arsvechkarev.vault.features.creating_entry.CreatingEntryUiEvent.OnLoginTextChanged
import com.arsvechkarev.vault.features.creating_entry.CreatingEntryUiEvent.OnWebsiteNameTextChanged
import com.arsvechkarev.vault.viewbuilding.Colors
import com.arsvechkarev.vault.viewbuilding.Dimens.IconPadding
import com.arsvechkarev.vault.viewbuilding.Dimens.ImageServiceNameSize
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginLarge
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginNormal
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginTiny
import com.arsvechkarev.vault.viewbuilding.Styles.AccentTextView
import com.arsvechkarev.vault.viewbuilding.Styles.BaseEditText
import com.arsvechkarev.vault.viewbuilding.Styles.BoldTextView
import com.arsvechkarev.vault.viewbuilding.Styles.Button
import com.arsvechkarev.vault.viewbuilding.TextSizes
import navigation.BaseFragmentScreen
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.circleRippleBackground
import viewdsl.constraints
import viewdsl.gravity
import viewdsl.hideKeyboard
import viewdsl.id
import viewdsl.image
import viewdsl.invisible
import viewdsl.layoutGravity
import viewdsl.margin
import viewdsl.margins
import viewdsl.onClick
import viewdsl.onLayoutChanged
import viewdsl.onSubmit
import viewdsl.onTextChanged
import viewdsl.padding
import viewdsl.setSoftInputMode
import viewdsl.showKeyboard
import viewdsl.text
import viewdsl.textColor
import viewdsl.textSize
import viewdsl.visible
import viewdsl.withViewBuilder
import kotlin.math.abs

class CreatingEntryScreen : BaseFragmentScreen() {
  
  override fun buildLayout(context: Context) = context.withViewBuilder {
    RootConstraintLayout {
      onLayoutChanged {
        showOrHideViewsBasedOnLayout()
      }
      HorizontalLayout(MatchParent, WrapContent) {
        id(ToolbarId)
        margins(top = StatusBarHeight)
        constraints {
          topToTopOf(parent)
        }
        ImageView(WrapContent, WrapContent) {
          image(R.drawable.ic_back)
          margin(MarginNormal)
          gravity(CENTER_VERTICAL)
          padding(IconPadding)
          circleRippleBackground(Colors.Ripple)
          onClick { store.tryDispatch(OnBackButtonClicked) }
        }
        TextView(WrapContent, WrapContent, style = BoldTextView) {
          layoutGravity(CENTER)
          text(R.string.text_new_password)
          textSize(TextSizes.H1)
        }
      }
      ImageView(ImageServiceNameSize, ImageServiceNameSize) {
        id(ImageId)
        scaleType = FIT_XY
        constraints {
          startToStartOf(parent)
          endToEndOf(parent)
          topToBottomOf(ToolbarId)
          bottomToTopOf(EditTextLayoutsId)
        }
      }
      VerticalLayout(MatchParent, WrapContent) {
        id(EditTextLayoutsId)
        constraints {
          centeredWithin(parent)
        }
        margins(top = ImageServiceNameSize * 2)
        TextView(WrapContent, WrapContent, style = AccentTextView) {
          id(TextWebsiteName)
          margins(start = MarginNormal)
          text(R.string.text_website_name)
        }
        EditText(MatchParent, WrapContent) {
          apply(BaseEditText(hint = R.string.hint_website_name))
          id(EditTextWebsiteName)
          // TODO (8/14/2022): Figure out problem with EditText margins
          margins(start = MarginNormal - MarginTiny, end = MarginNormal)
          onTextChanged { text -> store.tryDispatch(OnWebsiteNameTextChanged(text)) }
          onSubmit { editText(EditTextLogin).requestFocus() }
        }
        TextView(WrapContent, WrapContent, style = AccentTextView) {
          id(TextLogin)
          text(R.string.text_login)
          margins(start = MarginNormal, top = MarginLarge)
        }
        EditText(MatchParent, WrapContent, style = BaseEditText(hint = R.string.hint_login)) {
          id(EditTextLogin)
          margins(start = MarginNormal - MarginTiny, end = MarginNormal)
          onTextChanged { text -> store.tryDispatch(OnLoginTextChanged(text)) }
          onSubmit { continueWithCreating() }
        }
      }
      TextView(MatchParent, WrapContent, style = Button()) {
        id(ButtonContinue)
        margins(start = MarginNormal, end = MarginNormal, bottom = MarginNormal)
        constraints {
          startToStartOf(parent)
          endToEndOf(parent)
          bottomToBottomOf(parent)
        }
        text(R.string.text_continue)
        onClick { continueWithCreating() }
      }
    }
  }
  
  private val store by viewModelStore { CreatingEntryStore(appComponent) }
  
  override fun onInit() {
    store.subscribe(this, ::render)
  }
  
  override fun onAppearedOnScreenAfterAnimation() {
    editText(EditTextWebsiteName).apply {
      requestFocus()
      requireContext().showKeyboard(this)
    }
  }
  
  private fun render(state: CreatingEntryState) {
    imageView(ImageId).setServiceIcon(state.websiteName)
    if (state.websiteNameEmpty) {
      showAccentTextViewError(TextWebsiteName, R.string.text_website_name_cant_be_empty)
    } else {
      showAccentTextViewDefault(TextWebsiteName, R.string.text_website_name)
    }
    if (state.loginEmpty) {
      showAccentTextViewError(TextLogin, R.string.text_login_cant_be_empty)
    } else {
      showAccentTextViewDefault(TextLogin, R.string.text_login)
    }
  }
  
  override fun onDisappearedFromScreen() {
    editText(EditTextWebsiteName).clearFocus()
    editText(EditTextLogin).clearFocus()
    requireContext().hideKeyboard()
    requireContext().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)
  }
  
  private fun showOrHideViewsBasedOnLayout() {
    val imageView = imageView(ImageId)
    val spaceForImage = abs(view(TextWebsiteName).top - view(ToolbarId).bottom)
    if (spaceForImage < imageView.height) {
      imageView.invisible()
    } else {
      imageView.visible()
    }
    val continueButton = view(ButtonContinue)
    val marginBetweenButtonAndText = continueButton.top - view(EditTextLayoutsId).bottom
    if (marginBetweenButtonAndText < continueButton.height) {
      continueButton.invisible()
    } else {
      continueButton.visible()
    }
  }
  
  private fun continueWithCreating() {
    val websiteName = editText(EditTextWebsiteName).text.toString()
    val login = editText(EditTextLogin).text.toString()
    store.tryDispatch(OnContinueClicked(websiteName, login))
  }
  
  private fun showAccentTextViewDefault(textViewId: Int, @StringRes defaultTextRes: Int) {
    textView(textViewId).apply {
      apply(AccentTextView)
      text(defaultTextRes)
    }
  }
  
  private fun showAccentTextViewError(textViewId: Int, @StringRes errorTextRes: Int) {
    textView(textViewId).apply {
      textColor(Colors.Error)
      text(errorTextRes)
    }
  }
  
  private companion object {
    
    val ToolbarId = View.generateViewId()
    val EditTextLayoutsId = View.generateViewId()
    val ImageId = View.generateViewId()
    val TextWebsiteName = View.generateViewId()
    val TextLogin = View.generateViewId()
    val ButtonContinue = View.generateViewId()
    val EditTextWebsiteName = View.generateViewId()
    val EditTextLogin = View.generateViewId()
  }
}