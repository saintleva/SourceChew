package com.github.saintleva.sourcechew.data.secure

import com.github.saintleva.sourcechew.data.secure.SecureTokenStorage
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage


class DefaultTokenStorage(
    private val storage: SecureKeyValueStorage,
    private val key: String = "auth_token"
) : SecureTokenStorage {

    override suspend fun read(): String? =
        storage.read(key)

    override suspend fun write(token: String) =
        storage.write(key, token)

    override suspend fun clear() =
        storage.remove(key)
}