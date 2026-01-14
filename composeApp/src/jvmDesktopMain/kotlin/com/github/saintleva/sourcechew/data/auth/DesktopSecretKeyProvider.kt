package com.github.saintleva.sourcechew.data.auth

import com.github.saintleva.sourcechew.data.auth.SecretKeyProvider.Companion.KEY_SIZE
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class DesktopSecretKeyProvider(
    private val keyStorePath: Path,
    private val keyAlias: String,
    private val password: CharArray
) : SecretKeyProvider {

    companion object {
        const val KEYSTORE_TYPE_JCEKS = "JCEKS"
        const val AES_ALGORITHM = "AES"
    }

    override suspend fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_TYPE_JCEKS)

        if (Files.exists(keyStorePath)) {
            Files.newInputStream(keyStorePath).use {
                keyStore.load(it, password)
            }
        } else {
            keyStore.load(null, password)
        }

        val existingKey = keyStore.getKey(keyAlias, password)
        if (existingKey is SecretKey) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM)
        keyGenerator.init(KEY_SIZE)
        val secretKey = keyGenerator.generateKey()

        keyStore.setEntry(
            keyAlias,
            KeyStore.SecretKeyEntry(secretKey),
            KeyStore.PasswordProtection(password)
        )

        Files.createDirectories(keyStorePath.parent)
        Files.newOutputStream(keyStorePath).use {
            keyStore.store(it, password)
        }

        return secretKey
    }
}