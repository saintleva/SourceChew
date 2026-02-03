package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import ca.gosyer.appdirs.AppDirs
import com.github.saintleva.sourcechew.BuildConfig
import com.github.saintleva.sourcechew.data.auth.DesktopSecureTokenStorage
import com.github.saintleva.sourcechew.data.auth.SecureTokenStorage
import org.koin.dsl.module
import java.io.File


actual val platformModule = module {

    single<AppDirs> {
        AppDirs {
            appName = BuildConfig.APPLICATION_NAME
            appAuthor = BuildConfig.APPLICATION_AUTHOR
        }
    }

    single<File>(qualifier = DataStoreFileQualifier) {
        val configDir = get<AppDirs>().getUserConfigDir()
        val dataStoreFile = File(configDir, dataStoreFileName)
        if (!dataStoreFile.parentFile.exists()) {
            dataStoreFile.parentFile.mkdirs()
        }
        dataStoreFile
    }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            get<File>(qualifier = DataStoreFileQualifier)
        }
    }

    single<SecureTokenStorage> { DesktopSecureTokenStorage() }
}