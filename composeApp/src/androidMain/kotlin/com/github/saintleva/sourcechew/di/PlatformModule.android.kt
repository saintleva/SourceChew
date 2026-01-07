package com.github.saintleva.sourcechew.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.saintleva.sourcechew.data.auth.AndroidSecureTokenStorage
import com.github.saintleva.sourcechew.data.auth.SecureTokenStorage
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File


object SecureDataStoreQualifier : Qualifier {
    override val value: QualifierValue = "SecureDataStore"
}

private const val secureDataStoreFileName = "prefs.preferences_pb"

actual val platformModule = module {

    single<DataStore<Preferences>>(qualifier = ConfigDataStoreQualifier) {
        PreferenceDataStoreFactory.create {
            File(get<Context>().filesDir.resolve(dataStoreFileName).absolutePath)
        }
    }

    single<DataStore<Preferences>>(qualifier = SecureDataStoreQualifier) {
        PreferenceDataStoreFactory.create {
            File(get<Context>().filesDir.resolve(secureDataStoreFileName).absolutePath)
        }
    }

    single<SecureTokenStorage> {
        AndroidSecureTokenStorage(dataStore = get(qualifier = SecureDataStoreQualifier))
    }
}