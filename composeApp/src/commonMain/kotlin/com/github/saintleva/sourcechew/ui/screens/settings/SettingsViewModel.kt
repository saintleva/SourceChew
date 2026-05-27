package com.github.saintleva.sourcechew.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.defaultPaginationPageSize
import com.github.saintleva.sourcechew.domain.models.paginationPageSizeRange
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val configStore: ConfigStore) : ViewModel() {

    val accessor = configStore.appSettings

    val pageSize = accessor.paginationPageSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = defaultPaginationPageSize
    )

    fun onPageSizeChange(newValue: Int) {
        val validatedValue = newValue.coerceIn(paginationPageSizeRange)
        viewModelScope.launch {
            configStore.appSettings.changePaginationPageSize(validatedValue)
        }
    }
}