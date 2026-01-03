package com.github.saintleva.sourcechew.data.auth

import com.github.saintleva.sourcechew.domain.repository.AuthManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class RusshwolfAuthManager(private val settings: ObservableSettings) : AuthManager {

    companion object {
        private const val AUTH_TOKEN_KEY = "auth_token"
    }

    override val authToken: Flow<String?> = settings.getStringOrNullFlow(AUTH_TOKEN_KEY)

    override val isAuthorized: Flow<Boolean> = authToken.map { !it.isNullOrBlank() }

    override suspend fun saveToken(token: String) {
        settings.putString(AUTH_TOKEN_KEY, token)
    }

    override suspend fun clearToken() {
        settings.remove(AUTH_TOKEN_KEY)
    }
}
