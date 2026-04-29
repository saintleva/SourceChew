package com.github.saintleva.sourcechew.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.repository.AuthManager
import com.mobilebytelabs.kmptoolkit.clipboard.ClipboardManager
import com.mobilebytelabs.kmptoolkit.clipboard.copyToClipboard
import com.mobilebytelabs.kmptoolkit.clipboard.getFromClipboard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AuthViewModel(
    private val manager: AuthManager,
    private val clipboardManager: ClipboardManager
) : ViewModel() {

    val isAuthorized = manager.isAuthorized.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _savedToken = mutableStateOf<String?>(null)
    private val _token = mutableStateOf("")

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
        val current = token.value.trim()
        val saved = _savedToken.value?.trim() ?: ""

        current.isNotBlank() && current != saved
    }

    fun onTokenChange(newToken: String) {
        _token.value = newToken
    }

    fun onTokenSave() {
        if (!canSaveState.value) return
        viewModelScope.launch {
            manager.saveToken(token.value.trim())
        }
    }

    fun onTokenClear() {
        viewModelScope.launch {
            manager.clearToken()
        }
    }

    fun isClipboardNotEmpty() = clipboardManager.hasText()

    fun onTokenCopy() {
        viewModelScope.launch {
            clipboardManager.copyAsync(token.value)
        }
    }

    fun onTokenPaste() {
        viewModelScope.launch {
            clipboardManager.pasteAsync()?.let {
                onTokenChange(it)
            }
        }
    }
}