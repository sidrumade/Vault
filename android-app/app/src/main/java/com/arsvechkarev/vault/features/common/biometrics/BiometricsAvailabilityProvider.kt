package com.arsvechkarev.vault.features.common.biometrics

import android.content.Context
import androidx.biometric.BiometricManager

interface BiometricsAvailabilityProvider {
  
  fun isAvailable(): Boolean
}

class BiometricsAvailabilityProviderImpl(
  private val context: Context
) : BiometricsAvailabilityProvider {

  override fun isAvailable(): Boolean {
    val biometricManager = BiometricManager.from(context)
    return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
  }
}
