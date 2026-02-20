package com.github.saintleva.sourcechew.di

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.MultiplatformSettingsKeyValueStorage
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module


//TODO: Implement real DI
actual val platformModule = module {

    single<Settings> { StorageSettings() }

    single<SecureKeyValueStorage> {
        MultiplatformSettingsKeyValueStorage(settings = get())
    }
}

actual val ioDispatcher = Dispatchers.Default