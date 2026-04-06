package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.ClearableSecureKeyValueStorage
import eu.anifantakis.lib.ksafe.KSafe


class KSafeKeyValueStorage(private val ksafe: KSafe) : ClearableSecureKeyValueStorage {

    override suspend fun read(key: String): String? {
        return ksafe.get<String?>(key, null)
    }

    override suspend fun write(key: String, value: String) {
        ksafe.put(key, value)
    }

    override suspend fun remove(key: String) {
        ksafe.delete(key)
    }

    override suspend fun clearAll() {
        ksafe.clearAll()
    }
}