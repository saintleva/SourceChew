package com.github.saintleva.sourcechew.data.auth

import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException
import com.github.saintleva.sourcechew.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DesktopSecureTokenStorage(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SecureTokenStorage {

    companion object {
        private const val ACCOUNT = "auth_token"
    }

    private val keyring = Keyring.create()

    override suspend fun read(): String? {
        return withContext(ioDispatcher) {
            try {
                keyring.getPassword(BuildConfig.APPLICATION_NAME, ACCOUNT)
            } catch (e: PasswordAccessException) {
                null
            }
        }
    }

    override suspend fun write(token: String) {
        withContext(ioDispatcher) {
            keyring.setPassword(BuildConfig.APPLICATION_NAME, ACCOUNT, token)
        }
    }

    override suspend fun clear() {
        withContext(ioDispatcher) {
            keyring.deletePassword(BuildConfig.APPLICATION_NAME, ACCOUNT)
        }
    }
} 