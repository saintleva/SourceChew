package com.github.saintleva.sourcechew.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module
import java.io.File


actual val platformModule = module {

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            File(get<Context>().filesDir.resolve(dataStoreFileName).absolutePath)
        }
    }
}