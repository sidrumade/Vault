package com.arsvechkarev.vault.core

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private const val ALGORITHM = "AES"
private const val SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256"
private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"
private const val SHA_224 = "SHA-224"
private const val SHA_256 = "SHA-256"
private const val SECRET_KEY_LENGTH = 256
private const val SECRET_KEY_ITERATIONS = 72361
private const val SALT_ITERATIONS = 89464
private const val IV_ITERATIONS = 131796

private val cipher: Cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
private val secretKeyFactory: SecretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM)
private val sha224 = MessageDigest.getInstance(SHA_224)
private val sha256 = MessageDigest.getInstance(SHA_256)

fun encryptText(password: String, plaintext: String): String {
  val passwordByteArray = password.toByteArray()
  val secretKey = getKeyFromPassword(password, getSaltFromPassword(passwordByteArray))
  val initVector = getIvFromPassword(passwordByteArray)
  cipher.init(Cipher.ENCRYPT_MODE, secretKey, initVector)
  return Base64.encodeToString(cipher.doFinal(plaintext.toByteArray()), Base64.DEFAULT)
}

fun decryptText(password: String, ciphertext: String): String {
  val passwordByteArray = password.toByteArray()
  val secretKey = getKeyFromPassword(password, getSaltFromPassword(passwordByteArray))
  val initVector = getIvFromPassword(passwordByteArray)
  cipher.init(Cipher.DECRYPT_MODE, secretKey, initVector)
  return String(cipher.doFinal(Base64.decode(ciphertext, Base64.DEFAULT)))
}

private fun getKeyFromPassword(password: String, salt: ByteArray): SecretKey {
  val spec = PBEKeySpec(password.toCharArray(), salt, SECRET_KEY_ITERATIONS, SECRET_KEY_LENGTH)
  return SecretKeySpec(secretKeyFactory.generateSecret(spec).encoded, ALGORITHM)
}

private fun getSaltFromPassword(passwordByteArray: ByteArray): ByteArray {
  var buff = sha256.digest(passwordByteArray)
  repeat(SALT_ITERATIONS) { buff = sha256.digest(buff) }
  return buff
}

private fun getIvFromPassword(passwordByteArray: ByteArray): IvParameterSpec {
  var buff = sha224.digest(passwordByteArray)
  repeat(IV_ITERATIONS) { buff = sha224.digest(buff) }
  val ivBuffer = ByteArray(16) { i -> buff[i] }
  return IvParameterSpec(ivBuffer)
}