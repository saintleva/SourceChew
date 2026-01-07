package com.github.saintleva.sourcechew.data.auth

import com.github.saintleva.sourcechew.domain.repository.AuthManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private val Dispatchers.IO: CoroutineDispatcher

class AuthManagerImpl(
    private val storage: SecureTokenStorage,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthManager {

    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())

    private val _authToken = MutableStateFlow<String?>(null)
    override val authToken = _authToken.asStateFlow()

    override val isAuthorized: Flow<Boolean> = authToken
        .map { !it.isNullOrBlank() }
        .distinctUntilChanged()

    init {
        scope.launch {
            _authToken.value = storage.read()
        }
    }

    override suspend fun saveToken(token: String) {
        storage.write(token)
        _authToken.value = token
    }

    override suspend fun clearToken() {
        storage.clear()
        _authToken.value = null
    }
}
