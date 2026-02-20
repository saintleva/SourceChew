package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.di.ioDispatcher
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class MultiplatformSettingsKeyValueStorage(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher = ioDispatcher
) : SecureKeyValueStorage {

    override suspend fun read(key: String): String? {
        return withContext(dispatcher) {
            settings.getStringOrNull(key)
        }
    }

    override suspend fun write(key: String, value: String) {
        withContext(dispatcher) {
            settings.putString(key, value)
        }
    }

    override suspend fun remove(key: String) {
        withContext(dispatcher) {
            settings.remove(key)
        }
    }
}