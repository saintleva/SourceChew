package com.github.saintleva.sourcechew.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SettingsViewModel(private val appSettingsStore: ConfigStore<AppSettings>) : ViewModel() {

    val settings = appSettingsStore.config.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = AppSettings.default
    )

    fun onPageSizeChange(newValue: Int) {
        val validatedValue = newValue.coerceIn(AppSettings.paginationPageSizeRange)
        viewModelScope.launch {
            appSettingsStore.update { it.copy(paginationPageSize = validatedValue) }
        }
    }
}