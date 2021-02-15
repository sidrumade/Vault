package com.arsvechkarev.vault.features.creating_master_password

import com.arsvechkarev.vault.core.BasePresenter
import com.arsvechkarev.vault.core.Threader
import com.arsvechkarev.vault.features.common.UserAuthSaver
import com.arsvechkarev.vault.core.extensions.assertThat
import com.arsvechkarev.vault.features.creating_master_password.CreateMasterPasswordScreenState.ENTERING_PASSWORD
import com.arsvechkarev.vault.features.creating_master_password.CreateMasterPasswordScreenState.REPEATING_PASSWORD
import com.arsvechkarev.vault.cryptography.MasterPasswordChecker
import com.arsvechkarev.vault.cryptography.MasterPasswordHolder
import com.arsvechkarev.vault.cryptography.PasswordStatus.OK
import com.arsvechkarev.vault.cryptography.PasswordChecker

class CreateMasterPasswordPresenter(
  threader: Threader,
  private val passwordChecker: PasswordChecker,
  private val masterPasswordChecker: MasterPasswordChecker,
  private val userAuthSaver: UserAuthSaver
) : BasePresenter<CreateMasterPasswordView>(threader) {
  
  private var state = ENTERING_PASSWORD
  private var previouslyEnteredPassword: String = ""
  
  fun computePasswordStrength(password: String) {
    val strength = passwordChecker.checkStrength(password)
    viewState.showPasswordStrength(strength)
  }
  
  fun allowBackPress(): Boolean {
    if (state == REPEATING_PASSWORD) {
      state = ENTERING_PASSWORD
      viewState.switchToEnterPasswordState()
      return false
    }
    return true
  }
  
  fun onBackButtonClick() {
    if (state == REPEATING_PASSWORD) {
      state = ENTERING_PASSWORD
      viewState.switchToEnterPasswordState()
    }
  }
  
  fun onEnteredPassword(password: String) {
    assertThat(state == ENTERING_PASSWORD)
    val passwordStatus = passwordChecker.validate(password)
    if (passwordStatus == OK) {
      state = REPEATING_PASSWORD
      previouslyEnteredPassword = password
      viewState.switchToRepeatPasswordState()
    } else {
      viewState.showPasswordProblem(passwordStatus)
    }
  }
  
  fun onRepeatedPassword(password: String) {
    assertThat(state == REPEATING_PASSWORD)
    if (previouslyEnteredPassword != "" && password == previouslyEnteredPassword) {
      finishAuthorization()
    } else {
      viewState.showPasswordsDontMatch()
    }
  }
  
  private fun finishAuthorization() {
    updateViewState { showFinishingAuthorization() }
    onBackgroundThread {
      userAuthSaver.setUserIsAuthorized(true)
      MasterPasswordHolder.setMasterPassword(previouslyEnteredPassword)
      masterPasswordChecker.encodeSecretPhrase(previouslyEnteredPassword)
      updateViewState { goToPasswordsList() }
    }
  }
}