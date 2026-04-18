package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import ca.gosyer.appdirs.AppDirs
import com.github.saintleva.sourcechew.BuildConfig
import com.github.saintleva.sourcechew.data.secure.DesktopSecureKeyValueStorage
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.secure.SecureTokenStorage
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
            appAuthor = BuildConfig.APPLICATION_AUTHOR
        }
    }

    single<File>(qualifier = ConfigDataStoreFileQualifier) {
        val configDir = get<AppDirs>().getUserConfigDir()
        val dataStoreFile = File(configDir, dataStoreFileName)
        if (!dataStoreFile.parentFile.exists()) {
            dataStoreFile.parentFile.mkdirs()
        }
        dataStoreFile
    }

    single<DataStore<Preferences>>(qualifier = ConfigDataStoreQualifier) {
        PreferenceDataStoreFactory.create {
            get<File>(qualifier = ConfigDataStoreFileQualifier)
        }
    }

    single<SecureKeyValueStorage> { DesktopSecureKeyValueStorage() }
}