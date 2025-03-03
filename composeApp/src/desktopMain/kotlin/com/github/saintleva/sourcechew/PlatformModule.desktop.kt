package com.github.saintleva.sourcechew

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.saintleva.sourcechew.di.dataStoreFileName
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File


actual val platformModule: Module = module {

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            val file = File(System.getProperty("user.home"), ".sourcechew")
            if (!file.exists()) {
                file.mkdirs()
            }
            File(file, dataStoreFileName)
        }
    }
}