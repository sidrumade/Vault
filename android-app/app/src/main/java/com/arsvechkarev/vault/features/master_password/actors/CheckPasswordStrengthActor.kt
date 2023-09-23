package com.arsvechkarev.vault.features.master_password.actors

import buisnesslogic.PasswordInfoChecker
import com.arsvechkarev.vault.core.mvi.tea.Actor
import com.arsvechkarev.vault.features.master_password.MasterPasswordCommand
import com.arsvechkarev.vault.features.master_password.MasterPasswordEvent
import com.arsvechkarev.vault.features.master_password.MasterPasswordEvent.UpdatedPasswordStrength
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest

class CheckPasswordStrengthActor(
  private val passwordInfoChecker: PasswordInfoChecker,
) : Actor<MasterPasswordCommand, MasterPasswordEvent> {
  
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun handle(commands: Flow<MasterPasswordCommand>): Flow<MasterPasswordEvent> {
    return commands.filterIsInstance<MasterPasswordCommand.PasswordCommand.CheckPasswordStrength>()
        .mapLatest { command ->
          val passwordStrength = passwordInfoChecker.checkStrength(command.password)
          UpdatedPasswordStrength(passwordStrength)
        }
  }
}