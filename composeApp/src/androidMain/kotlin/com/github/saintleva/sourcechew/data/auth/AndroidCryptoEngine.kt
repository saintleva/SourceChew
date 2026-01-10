package com.github.saintleva.sourcechew.data.auth

import javax.crypto.Cipher
import javax.crypto.SecretKey


class AndroidCryptoEngine(private val key: SecretKey) : AesGcmCryptoEngine(key) {

    override fun createEncryptCipher(): Cipher {
        return Cipher.getInstance(ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, key)
        }
    }
}