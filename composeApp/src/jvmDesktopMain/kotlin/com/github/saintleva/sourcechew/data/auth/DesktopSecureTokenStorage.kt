package com.github.saintleva.sourcechew.data.auth

import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException
import com.github.saintleva.sourcechew.BuildConfig


object DesktopSecureTokenStorage : SecureTokenStorage {

    private val keyring = Keyring.create()
    private const val ACCOUNT = "auth_token"

    override suspend fun read(): String? {
        return try {
            keyring.getPassword(BuildConfig.APPLICATION_NAME, ACCOUNT)
        } catch (e: PasswordAccessException) {
            null
        }
    }

    override suspend fun write(token: String) {
        keyring.setPassword(BuildConfig.APPLICATION_NAME, ACCOUNT, token)
    }

    override suspend fun clear() {
        keyring.deletePassword(BuildConfig.APPLICATION_NAME, ACCOUNT)
    }
}