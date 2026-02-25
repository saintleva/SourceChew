package com.github.saintleva.sourcechew.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.github.saintleva.sourcechew.data.storage.DataStoreKeyValueStorage
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import io.github.osipxd.security.crypto.createEncrypted
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module
import java.io.File


object SecureDataStoreQualifier : Qualifier {
    override val value: QualifierValue = this::class.qualifiedName!!
}

private const val secureDataStoreFileName = "secure.preferences_pb"

actual val platformModule = module {

    single<DataStore<Preferences>>(qualifier = ConfigDataStoreQualifier) {
        PreferenceDataStoreFactory.create {
            File(get<Context>().filesDir.resolve(dataStoreFileName).absolutePath)
        }
    }

    single<DataStore<Preferences>>(qualifier = SecureDataStoreQualifier) {
        PreferenceDataStoreFactory.createEncrypted {
            EncryptedFile.Builder(
                get<Context>().preferencesDataStoreFile(secureDataStoreFileName),
                get<Context>(),
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
        }
    }

    single<SecureKeyValueStorage> {
        DataStoreKeyValueStorage(dataStore = get(qualifier = SecureDataStoreQualifier))
    }
}