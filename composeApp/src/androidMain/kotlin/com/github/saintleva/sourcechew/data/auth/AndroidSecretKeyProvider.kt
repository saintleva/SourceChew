package com.github.saintleva.sourcechew.data.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.github.saintleva.sourcechew.data.auth.SecretKeyProvider.Companion.KEY_SIZE
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class AndroidSecretKeyProvider(
    private val keyAlias: String
) : SecretKeyProvider {

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }

    override suspend fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        val existingKey = keyStore.getKey(keyAlias, null)
        if (existingKey is SecretKey) return existingKey

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)

        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(KEY_SIZE)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}