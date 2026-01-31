package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import ca.gosyer.appdirs.AppDirs
import com.github.saintleva.sourcechew.BuildConfig
import com.github.saintleva.sourcechew.data.auth.CryptoEngine
import com.github.saintleva.sourcechew.data.auth.DesktopSecretKeyProvider
import com.github.saintleva.sourcechew.data.auth.DesktopSecureTokenStorage
import com.github.saintleva.sourcechew.data.auth.JceksSecretKeyProvider
import com.github.saintleva.sourcechew.data.auth.SecretKeyProvider
import com.github.saintleva.sourcechew.data.auth.SecureTokenStorage
import org.koin.dsl.module
import java.io.File
import java.nio.file.Paths


const val keyStoreFileName = "keystore.jceks"
const val keyAlias = "auth-key"

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

    single<SecretKeyProvider> {
        val dataDir = get<AppDirs>().getUserDataDir()
        val keyStorePath = Paths.get(dataDir, keyStoreFileName)
        val keyAlias = keyAlias
        val password = "password".toCharArray()

        if (!keyStorePath.parent.toFile().exists()) {
            keyStorePath.parent.toFile().mkdirs()
        }

        JceksSecretKeyProvider(
            keyStorePath = keyStorePath,
            keyAlias = keyAlias,
            password = password
        )
    }

    single<CryptoEngine> {
        DesktopCryptoEngine(key = get<SecretKeyProvider>().getOrCreateKey())
    }

    single<SecureTokenStorage> {
        val dataDir = get<AppDirs>().getUserDataDir()
        DesktopSecureTokenStorage(
            storageDir = Paths.get(dataDir),
            crypto = get()
        )
    }
}