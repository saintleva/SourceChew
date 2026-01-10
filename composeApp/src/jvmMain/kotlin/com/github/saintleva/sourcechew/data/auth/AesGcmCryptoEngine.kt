package com.github.saintleva.sourcechew.data.auth

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


abstract class AesGcmCryptoEngine(
    private val key: SecretKey
) : CryptoEngine {

    companion object {
        const val ALGORITHM = "AES/GCM/NoPadding"
        const val IV_SIZE_BYTES = 12
        const val TAG_SIZE_BITS = 128
    }

    protected open fun createEncryptCipher(): Cipher {
        val iv = ByteArray(IV_SIZE_BYTES).also {
            SecureRandom().nextBytes(it)
        }

        return Cipher.getInstance(ALGORITHM).apply {
            init(
                Cipher.ENCRYPT_MODE,
                key,
                GCMParameterSpec(TAG_SIZE_BITS, iv)
            )
        }
    }

    protected open fun createDecryptCipher(iv: ByteArray): Cipher =
        Cipher.getInstance(ALGORITHM).apply {
            init(
                Cipher.DECRYPT_MODE,
                key,
                GCMParameterSpec(TAG_SIZE_BITS, iv)
            )
        }

    override fun encrypt(data: ByteArray): ByteArray {
        val cipher = createEncryptCipher()
        val cipherText = cipher.doFinal(data)

        val iv = cipher.iv
        require(iv.size == IV_SIZE_BYTES)

        return iv + cipherText
    }

    override fun decrypt(encrypted: ByteArray): ByteArray {
        require(encrypted.size > IV_SIZE_BYTES)

        val iv = encrypted.copyOfRange(0, IV_SIZE_BYTES)
        val cipherText = encrypted.copyOfRange(IV_SIZE_BYTES, encrypted.size)

        val cipher = createDecryptCipher(iv)
        return cipher.doFinal(cipherText)
    }
}