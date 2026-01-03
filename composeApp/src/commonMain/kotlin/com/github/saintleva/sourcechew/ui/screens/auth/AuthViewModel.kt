package com.github.saintleva.sourcechew.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.repository.AuthManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AuthViewModel(private val manager: AuthManager) : ViewModel() {

    val isAuthorized = manager.isAuthorized.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _token = mutableStateOf("") //TODO: Do I need initial value here?

    val token: State<String> = _token

    init {
        viewModelScope.launch {
            manager.authToken.collect {
                _token.value = it ?: ""
            }
        }
    }

    fun canSave() = token.value.isNotBlank()

    fun onTokenChange(newToken: String) {
        _token.value = newToken
    }

    fun onTokenSave() {
        if (!canSave()) return
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