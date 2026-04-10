package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.MultiplatformSettingsKeyValueStorage
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule = module {

    single<Settings> {
        val bundleId = NSBundle.mainBundle.bundleIdentifier ?: "com.github.saintleva.sourcechew"
        KeychainSettings(service = bundleId)
    }

    single<SecureKeyValueStorage> {
        MultiplatformSettingsKeyValueStorage(settings = get())
    }

    single<DataStore<Preferences>>(qualifier = ConfigDataStoreQualifier) {
        PreferenceDataStoreFactory.createWithPath(
            scope = CoroutineScope(ioDispatcher + SupervisorJob()),
            produceFile = {
                val directory = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = true,
                    error = null
                )
                val path = requireNotNull(directory?.path) + "/$dataStoreFileName.preferences_pb"
                path.toPath()
            }
        )
    }
}

actual val ioDispatcher = Dispatchers.Default
