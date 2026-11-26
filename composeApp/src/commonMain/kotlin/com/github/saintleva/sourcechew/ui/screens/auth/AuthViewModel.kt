package com.github.saintleva.sourcechew.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.repository.AuthRepository
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import com.mobilebytelabs.kmptoolkit.clipboard.ClipboardManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val clipboardManager: ClipboardManager
) : ViewModel() {

    val isAuthorized: StateFlow<Boolean> = repository.isAuthorized.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = false
    )

    private val _savedToken = MutableStateFlow<String?>(null)

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token.asStateFlow()

    val canSaveState: StateFlow<Boolean> = combine(_token, _savedToken) { current, saved ->
        val trimmedCurrent = current.trim()
        val trimmedSaved = saved?.trim() ?: ""
        trimmedCurrent.isNotBlank() && trimmedCurrent != trimmedSaved
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = false
    )

    init {
        viewModelScope.launch {
            repository.authToken.collect { newValue ->
                val updatedValue = newValue ?: ""
                val oldSaved = _savedToken.value ?: ""
                _savedToken.value = newValue
                if (_token.value.isBlank() || _token.value == oldSaved) {
                    _token.value = updatedValue
                }
            }
        }
    }

    fun onTokenChange(newToken: String) {
        _token.value = newToken
    }

    fun onTokenSave() {
        if (!canSaveState.value) return
        viewModelScope.launch {
            repository.saveToken(_token.value.trim())
        }
    }

    fun onTokenClear() {
        viewModelScope.launch {
            repository.clearToken()
        }
    }

    fun isClipboardNotEmpty(): Boolean = clipboardManager.hasText()

    fun onTokenCopy() {
        viewModelScope.launch {
            clipboardManager.copyAsync(_token.value)
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
