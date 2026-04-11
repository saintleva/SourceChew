package com.github.saintleva.sourcechew.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.repository.AuthManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AuthViewModel(private val manager: AuthManager) : ViewModel() {

    val isAuthorized = manager.isAuthorized.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _savedToken = mutableStateOf<String?>(null)
    private val _token = mutableStateOf("") //TODO: Do I need initial value here?

    val token: State<String> = _token

    init {
        viewModelScope.launch {
            manager.authToken.collect { newValue ->
                val updatedValue = newValue ?: ""
                _savedToken.value = newValue
                if (_token.value.isBlank() || _token.value == _savedToken.value) {
                    _token.value = updatedValue
                }
            }
        }
    }

    val canSaveState = derivedStateOf {
        token.value.isNotBlank() && _savedToken.value != token.value
    }

    fun onTokenChange(newToken: String) {
        _token.value = newToken
    }

    fun onTokenSave() {
        if (!canSaveState.value) return
        viewModelScope.launch {
            manager.saveToken(token.value)
        }
    }

    fun onTokenClear() {
        viewModelScope.launch {
            manager.clearToken()
        }
    }
}