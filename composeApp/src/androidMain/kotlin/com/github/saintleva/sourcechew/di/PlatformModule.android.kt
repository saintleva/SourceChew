package com.github.saintleva.sourcechew.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.AppPreferences
import com.github.saintleva.sourcechew.data.storage.KSafeKeyValueStorage
import eu.anifantakis.lib.ksafe.KSafe
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.io.path.toPath
import kotlin.io.resolve


//TODO: Use or remove this
//object SecureDataStoreQualifier : Qualifier {
//    override val value: QualifierValue = this::class.qualifiedName!!
//}
//
//private const val secureDataStoreFileName = "secure.preferences_pb"

actual val platformModule = module {

    single<Context> { androidContext() }

    single<DataStore<AppPreferences>> {
        println("DATASTORE CREATED")

        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                // Koin automatically resolves OkioSerializer<AppPreferences> provided in DomainModule
                serializer = get(),
                producePath = {
                    // Store in the app's internal files directory
                    get<Context>().filesDir.resolve(PREFS_DATA_STORE_FILE_NAME).toOkioPath()
                }
            )
        )
    }

    //TODO: Use or remove this
//    single<DataStore<Preferences>>(qualifier = SecureDataStoreQualifier) {
//
//        val context = get<Context>()
//
//        @Suppress("DEPRECATION")
//        val masterKey = MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build()
//
//        @Suppress("DEPRECATION")
//        PreferenceDataStoreFactory.createEncrypted {
//            EncryptedFile.Builder(
//                context.preferencesDataStoreFile(secureDataStoreFileName),
//                context,
//                masterKey.toString(),
//                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
//            ).build()
//        }
//    }
//    single<SecureKeyValueStorage> {
//        DataStoreKeyValueStorage(dataStore = get(qualifier = SecureDataStoreQualifier))
//    }

    single<KSafe> { KSafe(get()) }

    single<SecureKeyValueStorage> {
        KSafeKeyValueStorage(ksafe = get())
    }
}

// Module factory used in tests to inject a custom Context
fun createPlatformModule(externalContext: Context? = null) = module {
    includes(platformModule)
    externalContext?.let { context ->
        single<Context> { context }
    }
}