package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import ca.gosyer.appdirs.AppDirs
import com.github.saintleva.sourcechew.BuildKonfig
import com.github.saintleva.sourcechew.data.secure.DesktopSecureKeyValueStorage
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.AppPreferences
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module
import java.io.File


object ConfigDataStoreFileQualifier : Qualifier {
    override val value: QualifierValue = this::class.qualifiedName!!
}

actual val platformModule = module {

    single<AppDirs> {
        AppDirs {
            appName = BuildKonfig.APPLICATION_NAME
            appAuthor = BuildKonfig.APPLICATION_AUTHOR
        }
    }

    single<DataStore<AppPreferences>> {
        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                // Koin resolves OkioSerializer<AppPreferences> provided in DomainModule
                serializer = get(),
                producePath = {
                    val configDir = get<AppDirs>().getUserConfigDir()
                    val dataStoreFile = File(configDir, PREFS_DATA_STORE_FILE_NAME)
                    if (!dataStoreFile.parentFile.exists()) {
                        dataStoreFile.parentFile.mkdirs()
                    }
                    dataStoreFile.toOkioPath()
                }
            )
        )
    }

    single<SecureKeyValueStorage> { DesktopSecureKeyValueStorage() }
}