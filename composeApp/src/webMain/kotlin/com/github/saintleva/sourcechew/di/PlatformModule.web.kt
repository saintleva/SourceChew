package com.github.saintleva.sourcechew.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.WebLocalStorage
import com.github.saintleva.sourcechew.data.storage.AppPreferences
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module


val webCommonModule = module {

    single<DataStore<AppPreferences>> {
        DataStoreFactory.create(
            // Use WebLocalStorage which writes directly to the browser's window.localStorage
            storage = WebLocalStorage(
                // Koin resolves OkioSerializer<AppPreferences> provided in DomainModule
                serializer = get(),
                name = PREFS_DATA_STORE_FILE_NAME
            )
        )
    }
}

actual val ioDispatcher = Dispatchers.Default