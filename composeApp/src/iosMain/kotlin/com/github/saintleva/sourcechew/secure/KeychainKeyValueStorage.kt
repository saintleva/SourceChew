package com.github.saintleva.sourcechew.secure

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.di.ioDispatcher
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import kotlinx.coroutines.CoroutineDispatcher


class KeychainKeyValueStorage(
    private val dispatcher: CoroutineDispatcher = ioDispatcher
) : SecureKeyValueStorage {

    companion object {
        @OptIn(ExperimentalSettingsImplementation::class)
        private val settings = KeychainSettings()
    }

    override suspend fun read(key: String): String? {
        TODO("Not yet implemented")
    }

    override suspend fun write(key: String, value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(key: String) {
        TODO("Not yet implemented")
    }
}