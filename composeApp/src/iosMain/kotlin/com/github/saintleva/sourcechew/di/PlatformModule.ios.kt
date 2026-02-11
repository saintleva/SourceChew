package com.github.saintleva.sourcechew.di

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.secure.KeychainKeyValueStorage
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module


//TODO: Implement real DI
actual val platformModule = module {
    single<SecureKeyValueStorage> {
        KeychainKeyValueStorage()
    }
}

actual val ioDispatcher = Dispatchers.Default