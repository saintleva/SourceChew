package com.github.saintleva.sourcechew.di

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.MultiplatformSettingsKeyValueStorage
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.dsl.module


actual val platformModule = module {

    single<Settings> { StorageSettings() }

    single<SecureKeyValueStorage> {
        MultiplatformSettingsKeyValueStorage(settings = get())
    }
}