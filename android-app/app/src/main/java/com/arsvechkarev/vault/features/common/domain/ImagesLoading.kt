package com.arsvechkarev.vault.features.common.domain

import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import com.arsvechkarev.vault.BuildConfig
import com.arsvechkarev.vault.MainActivity
import com.arsvechkarev.vault.core.views.drawables.BaseShimmerDrawable.Companion.setShimmerDrawable
import com.arsvechkarev.vault.core.views.drawables.BaseShimmerDrawable.Companion.stopShimmerDrawable
import com.arsvechkarev.vault.core.views.drawables.LetterInCircleDrawable
import com.arsvechkarev.vault.core.views.drawables.LoadingPlaceholderDrawable
import com.arsvechkarev.vault.features.common.di.CoreComponentHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val BASE_URL_ICON_FILES = "https://raw.githubusercontent.com/arsvechkarev/Vault/master/icons/files/"
private const val EXTENSION_PNG = ".png"

fun ImageView.setIconForTitle(text: String, onImageLoadingFailed: () -> Unit) {
  val trimmedText = text.trim()
  if (trimmedText.isEmpty()) {
    return
  }
  val imagesNamesLoader = CoreComponentHolder.coreComponent.imagesNamesLoader
  val scope = (context as MainActivity).lifecycleScope
  scope.launch {
    val imagesNames = imagesNamesLoader.getFromCacheIfExists()
    if (imagesNames != null) {
      trySetImageFromMatchingNames(trimmedText, imagesNames, onImageLoadingFailed)
    } else {
      setShimmerDrawable(LoadingPlaceholderDrawable(text.first().toString()))
      if (BuildConfig.DEBUG) {
        delay(3000)
      }
      imagesNamesLoader.loadFromNetwork()
          .onSuccess { loadedImagesNames ->
            trySetImageFromMatchingNames(trimmedText, loadedImagesNames, onImageLoadingFailed)
          }
          .onFailure {
            onImageLoadingFailed()
            setLetterInCircleDrawable(trimmedText.first().toString())
          }
    }
  }
}

suspend fun ImageView.trySetImageFromMatchingNames(
  text: String,
  imagesNames: Set<String>,
  onImageLoadingFailed: () -> Unit
) {
  val matchingName = imagesNames.find { name -> text.contains(name) }
  if (matchingName == null) {
    setLetterInCircleDrawable(text.first().toString())
    return
  }
  val imagesCache = CoreComponentHolder.coreComponent.imagesCache
  val cachedImage = imagesCache.getImage(matchingName)
  if (cachedImage != null) {
    setImageDrawable(cachedImage)
    return
  }
  val url = BASE_URL_ICON_FILES + matchingName + EXTENSION_PNG
  load(url, builder = {
    crossfade(true)
    memoryCachePolicy(CachePolicy.DISABLED)
    diskCachePolicy(CachePolicy.DISABLED)
    placeholder(LoadingPlaceholderDrawable(text.first().toString()).apply { start() })
    error(LetterInCircleDrawable(text.first().toString()))
    listener(
      onSuccess = { _, result ->
        (context as? MainActivity?)?.lifecycleScope?.launch {
          imagesCache.saveImage(matchingName, result.drawable)
        }
        stopShimmerDrawable()
      },
      onCancel = { stopShimmerDrawable() },
      onError = { _, _ ->
        onImageLoadingFailed()
        stopShimmerDrawable()
      })
  })
}

private fun ImageView.setLetterInCircleDrawable(text: String) {
  val letter = text[0].toString()
  (this.drawable as? LetterInCircleDrawable)?.setLetter(letter) ?: run {
    setImageDrawable(LetterInCircleDrawable(letter))
  }
}