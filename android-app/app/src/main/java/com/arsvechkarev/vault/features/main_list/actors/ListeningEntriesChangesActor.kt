package com.arsvechkarev.vault.features.main_list.actors

import com.arsvechkarev.vault.core.ScreenState
import com.arsvechkarev.vault.core.mvi.tea.Actor
import com.arsvechkarev.vault.features.common.data.storage.ListenableCachedEntriesStorage
import com.arsvechkarev.vault.features.main_list.MainListCommand
import com.arsvechkarev.vault.features.main_list.MainListEvent
import com.arsvechkarev.vault.features.main_list.domain.EntriesMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ListeningEntriesChangesActor(
  private val passwordStorage: ListenableCachedEntriesStorage,
  private val entriesMapper: EntriesMapper
) : Actor<MainListCommand, MainListEvent> {
  
  override fun handle(commands: Flow<MainListCommand>): Flow<MainListEvent> {
    return passwordStorage.entries.map { entries ->
      val entriesItems = entriesMapper.mapEntries(entries)
      val state = if (entriesItems.isNotEmpty()) {
        ScreenState.success(entriesItems)
      } else {
        ScreenState.empty()
      }
      MainListEvent.UpdateData(state)
    }
  }
}