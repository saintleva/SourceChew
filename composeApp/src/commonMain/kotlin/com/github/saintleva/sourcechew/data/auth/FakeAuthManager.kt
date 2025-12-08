package com.github.saintleva.sourcechew.data.auth

import com.github.saintleva.sourcechew.domain.repository.AuthManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update


/**
 * A fake implementation of [AuthManager] that stores the token in memory.
 * Useful for development and testing UI flows without needing a real secure storage.
 *
 * NOTE: Data will be lost when the app restarts.
 */
object FakeAuthManager : AuthManager {

    // TODO: You can hardcode a token here for quick testing, but DO NOT commit real secrets to Git!
    // Example: private val initialToken = "ghp_your_test_token"
    private val initialToken: String? =
        "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"

    private val _authToken = MutableStateFlow(initialToken)
    override val authToken: Flow<String?> = _authToken.asStateFlow()

    override val isAuthorized: Flow<Boolean> = _authToken.map { token ->
        !token.isNullOrBlank()
    }

    override suspend fun saveToken(token: String) {
        // In a real implementation, this would save to EncryptedSharedPreferences/Keychain.
        _authToken.update { token }
    }

    override suspend fun clearToken() {
        _authToken.update { null }
    }
}