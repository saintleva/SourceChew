package com.github.saintleva.sourcechew.ui.screens.details.repo

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsRepository
import com.github.saintleva.sourcechew.ui.screens.details.DetailsUiState
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RepoDetailsViewModel(
    val owner: String,
    val name: String,
    private val repository: RepoDetailsRepository,
    appSettingsStore: ConfigStore<AppSettings>
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState<RepoDetails>>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState<RepoDetails>> = _uiState.asStateFlow()

    val settings = appSettingsStore.config.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = AppSettings.default
    )

    init {
        loadDetails()
    }

    fun loadDetails(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh || _uiState.value !is DetailsUiState.Success) {
                _uiState.value = DetailsUiState.Loading
            }
            try {
                when (val result = repository.getRepoDetails(owner, name, forceRefresh)) {
                    is Result.Success -> {
                        _uiState.value = DetailsUiState.Success(result.value)
                    }
                    is Result.Failure -> {
                        _uiState.value = DetailsUiState.Error(result.error)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: AppException) {
                _uiState.value = DetailsUiState.InfrastructureError(e)
            }
        }
    }

    fun refresh() {
        loadDetails(forceRefresh = true)
    }
}
