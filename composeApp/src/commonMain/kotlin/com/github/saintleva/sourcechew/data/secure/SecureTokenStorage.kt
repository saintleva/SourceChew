package com.github.saintleva.sourcechew.data.secure


interface SecureTokenStorage {
    suspend fun read(): String?
    suspend fun write(token: String)
    suspend fun clear()
}