package com.github.saintleva.sourcechew.data.auth

import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec


abstract class AesGcmCryptoEngine(
    private val key: javax.crypto.SecretKey
) : CryptoEngine {

    companion object {
        const val ALGORITHM = "AES/GCM/NoPadding"
        const val IV_SIZE_BYTES = 12
        const val TAG_LENGTH_BITS = 128
    }

    protected abstract fun generateIv(): ByteArray

    override fun encrypt(data: ByteArray): ByteArray {
        val iv = generateIv()

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            key,
            GCMParameterSpec(TAG_LENGTH_BITS, iv)
        )

        val encrypted = cipher.doFinal(data)
        return iv + encrypted
    }

    override fun decrypt(encrypted: ByteArray): ByteArray {
        require(encrypted.size > IV_SIZE_BYTES) { "Invalid encrypted payload" }

        val iv = encrypted.copyOfRange(0, IV_SIZE_BYTES)
        val cipherText = encrypted.copyOfRange(IV_SIZE_BYTES, encrypted.size)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            GCMParameterSpec(TAG_LENGTH_BITS, iv)
        )

        return cipher.doFinal(cipherText)
    }
}