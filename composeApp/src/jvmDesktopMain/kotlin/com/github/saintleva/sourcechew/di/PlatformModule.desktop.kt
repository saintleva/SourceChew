package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import ca.gosyer.appdirs.AppDirs
import com.github.saintleva.sourcechew.data.auth.CryptoEngine
import com.github.saintleva.sourcechew.data.auth.DesktopCryptoEngine
import com.github.saintleva.sourcechew.data.auth.DesktopSecretKeyProvider
import com.github.saintleva.sourcechew.data.auth.DesktopSecureTokenStorage
import com.github.saintleva.sourcechew.data.auth.SecretKeyProvider
import com.github.saintleva.sourcechew.data.auth.SecureTokenStorage
import org.koin.dsl.module
import java.io.File
import java.nio.file.Paths


const val appName = "SourceChew"
const val appAuthor = "saintleva"

actual val platformModule = module {

    single<File>(qualifier = DataStoreFileQualifier) {
        val appDirs = AppDirs {
            appName = appName
            appAuthor = appAuthor
        }
        val configDir = appDirs.getUserConfigDir()
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

    single<SecretKeyProvider> {
        val appDirs = AppDirs {
            appName = appName
            appAuthor = appAuthor
            version = "1.0"
        }
        val dataDir = appDirs.getUserDataDir()
        val keyStorePath = Paths.get(dataDir, "keystore.jceks")
        val keyAlias = "auth-key"
        val password = "password".toCharArray()

        if (!keyStorePath.parent.toFile().exists()) {
            keyStorePath.parent.toFile().mkdirs()
        }

        DesktopSecretKeyProvider(
            keyStorePath = keyStorePath,
            keyAlias = keyAlias,
            password = password
        )
    }

    single<CryptoEngine> {
        DesktopCryptoEngine(key = get<SecretKeyProvider>().getOrCreateKey())
    }

    single<SecureTokenStorage> {
        val appDirs = AppDirs {
            appName = appName
            appAuthor = appAuthor
            version = "1.0"
        }
        val dataDir = appDirs.getUserDataDir()

        DesktopSecureTokenStorage(
            storageDir = Paths.get(dataDir),
            crypto = get()
        )
    }
}