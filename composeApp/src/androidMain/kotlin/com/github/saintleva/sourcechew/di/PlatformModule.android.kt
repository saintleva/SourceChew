package com.github.saintleva.sourcechew.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.DataStoreKeyValueStorage
import io.github.osipxd.security.crypto.createEncrypted
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module
import java.io.File


object SecureDataStoreQualifier : Qualifier {
    override val value: QualifierValue = this::class.qualifiedName!!
}

private const val secureDataStoreFileName = "secure.preferences_pb"

fun createPlatformModule(externalContext: Context? = null) = module {

    single<Context> { externalContext ?: get() }

    single<DataStore<Preferences>>(qualifier = ConfigDataStoreQualifier) {
        PreferenceDataStoreFactory.create {
            get<Context>().preferencesDataStoreFile(dataStoreFileName)
            //TODO: Remove this
            //File(get<Context>().filesDir.resolve(dataStoreFileName).absolutePath)
        }
    }

    single<DataStore<Preferences>>(qualifier = SecureDataStoreQualifier) {

        val context = get<Context>()

        @Suppress("DEPRECATION")
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        @Suppress("DEPRECATION")
        PreferenceDataStoreFactory.createEncrypted {
            EncryptedFile.Builder(
                context.preferencesDataStoreFile(secureDataStoreFileName),
                context,
                masterKey.toString(),
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
        }
    }

    single<SecureKeyValueStorage> {
        DataStoreKeyValueStorage(dataStore = get(qualifier = SecureDataStoreQualifier))
    }
}

actual val platformModule = createPlatformModule()