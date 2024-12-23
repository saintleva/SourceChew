package com.github.saintleva.sourcechew.data.storage

import io.github.aakira.napier.Napier


object ConfigStorageMock : ConfigStorage {

    override suspend fun save(config: Config) {
        Napier.d(config.)
    }

    override suspend fun load(): Config {
        TODO("Not yet implemented")
    }
}