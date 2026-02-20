package com.github.saintleva.sourcechew.di

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.MultiplatformSettingsKeyValueStorage
import com.github.saintleva.sourcechew.secure.KeychainKeyValueStorage
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import platform.Foundation.NSBundle


actual val platformModule = module {

    single<Settings> {
        //TODO: Use better constant than hard-coded
        val bundleId = NSBundle.mainBundle.bundleIdentifier ?: "com.github.saintleva.sourcechew"
        KeychainSettings(service = bundleId)
    }

    single<SecureKeyValueStorage> {
        MultiplatformSettingsKeyValueStorage(settings = get())
    }
}

actual val ioDispatcher = Dispatchers.Default