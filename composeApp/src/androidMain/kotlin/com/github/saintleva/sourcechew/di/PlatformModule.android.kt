package com.github.saintleva.sourcechew.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File


actual val platformModule = module {

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            File(get<Context>().filesDir.resolve(dataStoreFileName).absolutePath)
        }
    }

    single<ObservableSettings> {
        //TODO: Do not use deprecated features
        val masterKey = MasterKey.Builder(androidContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedPrefs = EncryptedSharedPreferences.create(
            androidContext(),
            "auth_encrypted_prefs", // Имя файла
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        SharedPreferencesSettings(encryptedPrefs)
    }
}