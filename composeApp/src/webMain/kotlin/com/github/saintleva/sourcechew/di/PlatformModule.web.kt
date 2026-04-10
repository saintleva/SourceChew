package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import org.koin.dsl.module


val webCommonModule = module {
    single<DataStore<Preferences>>(qualifier = ConfigDataStoreQualifier) {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { "$dataStoreFileName.preferences_pb".toPath() }
        )
    }
}

actual val ioDispatcher = Dispatchers.Default