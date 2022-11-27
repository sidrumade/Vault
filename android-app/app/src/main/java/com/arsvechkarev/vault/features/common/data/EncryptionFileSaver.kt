package com.arsvechkarev.vault.features.common.data

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import buisnesslogic.FileSaver
import com.arsvechkarev.vault.core.DispatchersFacade
import kotlinx.coroutines.withContext
import java.io.File

class EncryptionFileSaver(
  private val filename: String,
  private val context: Context,
  private val masterKey: MasterKey,
  private val dispatchersFacade: DispatchersFacade,
) : FileSaver {
  
  override suspend fun saveData(data: ByteArray) = withContext(dispatchersFacade.IO) {
    val file = File(context.filesDir, filename)
    file.delete()
    val encryptedFile = getEncryptedFile(context, file)
    encryptedFile.openFileOutput().use { stream ->
      stream.write(data)
    }
  }
  
  override suspend fun readData(): ByteArray? = withContext(dispatchersFacade.IO) {
    val file = context.getFileStreamPath(filename)
    if (!file.exists()) {
      return@withContext null
    }
    getEncryptedFile(context, file).openFileInput().use { stream ->
      return@use stream.readBytes()
    }
  }
  
  override suspend fun getFileUri(): String {
    val file = File(context.filesDir, filename)
    check(file.exists())
    return file.toURI().toString()
  }
  
  override suspend fun delete(): Unit = withContext(dispatchersFacade.IO) {
    context.getFileStreamPath(filename).delete()
  }
  
  private fun getEncryptedFile(context: Context, file: File): EncryptedFile {
    val encryptionScheme = EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    return EncryptedFile.Builder(context, file, masterKey, encryptionScheme).build()
  }
}