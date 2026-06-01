package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.github.saintleva.sourcechew.BuildKonfig
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.AppPreferences
import com.github.saintleva.sourcechew.data.storage.MultiplatformSettingsKeyValueStorage
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


@OptIn(ExperimentalSettingsImplementation::class, ExperimentalForeignApi::class)
actual val platformModule = module {

    single<Settings> {
        val bundleId = NSBundle.mainBundle.bundleIdentifier ?: BuildKonfig.PACKAGE_NAME
        KeychainSettings(service = bundleId)
    }

    single<SecureKeyValueStorage> {
        MultiplatformSettingsKeyValueStorage(settings = get())
    }

    single<DataStore<AppPreferences>> {
        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                // Koin resolves OkioSerializer<AppPreferences> provided in DomainModule
                serializer = get(),
                producePath = {
                    // Get the URL for the app's document directory
                    val directory = NSFileManager.defaultManager.URLForDirectory(
                        directory = NSDocumentDirectory,
                        inDomain = NSUserDomainMask,
                        appropriateForURL = null,
                        create = true,
                        error = null
                    )

                    // Combine the path string and convert it to okio.Path
                    val stringPath = requireNotNull(directory?.path) + "/$PREFS_DATA_STORE_FILE_NAME"
                    stringPath.toPath()
                }
            )
        )
    }
}

actual val ioDispatcher = Dispatchers.Default
