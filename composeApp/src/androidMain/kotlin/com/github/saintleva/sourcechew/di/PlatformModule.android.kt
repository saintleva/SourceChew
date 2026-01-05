package com.github.saintleva.sourcechew.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.github.saintleva.sourcechew.data.auth.SecureTokenStorage
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File


actual val platformModule = module {

    //TODO: Implement "named()"
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            File(get<Context>().filesDir.resolve(dataStoreFileName).absolutePath)
        }
    }

    single<SecureTokenStorage> { SecureTokenStorage(dataStore = get()) }
}