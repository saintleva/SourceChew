package com.github.saintleva.sourcechew.data.secure

import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException
import com.github.saintleva.sourcechew.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DesktopSecureKeyValueStorage(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SecureKeyValueStorage {

    private val keyring by lazy { Keyring.create() }

    override suspend fun read(key: String): String? {
        return withContext(ioDispatcher) {
            try {
                keyring.getPassword(BuildConfig.APPLICATION_NAME, key)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun write(key: String, value: String) {
        withContext(ioDispatcher) {
            keyring.setPassword(BuildConfig.APPLICATION_NAME, key, value)
        }
    }

    override suspend fun remove(key: String) {
        withContext(ioDispatcher) {
            try {
                keyring.deletePassword(BuildConfig.APPLICATION_NAME, key)
            } catch (e: Exception) {} // Ignore if key is absent
        }
    }
} 