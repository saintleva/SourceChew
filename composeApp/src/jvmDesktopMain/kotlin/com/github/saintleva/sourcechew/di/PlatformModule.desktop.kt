package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import ca.gosyer.appdirs.AppDirs
import org.koin.dsl.module
import java.io.File


const val appName = "SourceChew"
const val appAuthor = "saintleva"

actual val platformModule = module {

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            val configDir = AppDirs(appName, appAuthor).getUserConfigDir()
            val file = File(configDir, dataStoreFileName)
            if (!file.exists()) {
                file.parentFile?.mkdirs()
            }
            file
        }
    }
}