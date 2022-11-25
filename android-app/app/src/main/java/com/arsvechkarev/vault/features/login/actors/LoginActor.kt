package com.arsvechkarev.vault.features.login.actors

import buisnesslogic.MasterPasswordChecker
import buisnesslogic.MasterPasswordHolder
import com.arsvechkarev.vault.core.mvi.tea.Actor
import com.arsvechkarev.vault.features.login.LoginCommand
import com.arsvechkarev.vault.features.login.LoginCommand.EnterWithMasterPassword
import com.arsvechkarev.vault.features.login.LoginEvent
import com.arsvechkarev.vault.features.login.LoginEvent.ShowFailureCheckingPassword
import com.arsvechkarev.vault.features.login.LoginEvent.ShowSuccessCheckingPassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest

class LoginActor(
  private val masterPasswordChecker: MasterPasswordChecker
) : Actor<LoginCommand, LoginEvent> {
  
  override fun handle(commands: Flow<LoginCommand>): Flow<LoginEvent> {
    return commands.filterIsInstance<EnterWithMasterPassword>()
        .mapLatest { command ->
          if (masterPasswordChecker.isCorrect(command.password)) {
            MasterPasswordHolder.setMasterPassword(command.password)
            ShowSuccessCheckingPassword
          } else {
            ShowFailureCheckingPassword
          }
        }
  }
}