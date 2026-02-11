package com.github.saintleva.sourcechew.secure

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.di.ioDispatcher
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import platform.Foundation.NSBundle


@OptIn(ExperimentalSettingsApi::class)
class KeychainKeyValueStorage(
    private val dispatcher: CoroutineDispatcher = ioDispatcher
) : SecureKeyValueStorage {

    private val settings: Settings

    init {
        val bundleId = NSBundle.mainBundle.bundleIdentifier ?: "com.github.saintleva.sourcechew"
        settings = KeychainSettings(service = bundleId)
    }

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