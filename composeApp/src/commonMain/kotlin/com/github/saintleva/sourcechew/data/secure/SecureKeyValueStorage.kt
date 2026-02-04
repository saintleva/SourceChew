package com.github.saintleva.sourcechew.data.secure

interface SecureKeyValueStorage {
    suspend fun read(key: String): String?
    suspend fun write(key: String, value: String)
    suspend fun remove(key: String)
}