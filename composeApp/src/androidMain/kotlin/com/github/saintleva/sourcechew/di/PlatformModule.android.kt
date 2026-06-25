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
import org.koin.dsl.module
import kotlin.io.path.toPath
import kotlin.io.resolve


//TODO: Use or remove this
//object SecureDataStoreQualifier : Qualifier {
//    override val value: QualifierValue = this::class.qualifiedName!!
//}
//
//private const val secureDataStoreFileName = "secure.preferences_pb"

fun createPlatformModule(externalContext: Context? = null) = module {

    //TODO: Is it right?
    externalContext?.let { context ->
        single<Context> { context }
    }

    single<DataStore<AppPreferences>> {
        println("DATASTORE CREATED")

        val context = get<Context>()

        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                // Koin automatically resolves OkioSerializer<AppPreferences> provided in DomainModule
                serializer = get(),
                producePath = {
                    // Store in the app's internal files directory
                    context.filesDir.resolve(PREFS_DATA_STORE_FILE_NAME).toOkioPath()
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

    single<KSafe> { KSafe(context = get()) }

    single<SecureKeyValueStorage> {
        KSafeKeyValueStorage(ksafe = get())
    }
}

actual val platformModule = createPlatformModule()