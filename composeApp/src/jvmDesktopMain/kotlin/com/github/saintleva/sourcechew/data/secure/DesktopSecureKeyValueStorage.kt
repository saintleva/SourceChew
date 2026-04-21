package com.github.saintleva.sourcechew.data.secure

import com.github.javakeyring.Keyring
import com.github.saintleva.sourcechew.BuildKonfig
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
                keyring.getPassword(BuildKonfig.APPLICATION_NAME, key)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun write(key: String, value: String) {
        withContext(ioDispatcher) {
            keyring.setPassword(BuildKonfig.APPLICATION_NAME, key, value)
        }
    }

    override suspend fun remove(key: String) {
        withContext(ioDispatcher) {
            runCatching {
                keyring.deletePassword(BuildKonfig.APPLICATION_NAME, key)
            }
        }
    }
} 