package com.github.saintleva.sourcechew.ui.screens.details.repo

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.usecase.GetRepoDetailsUseCase
import com.github.saintleva.sourcechew.ui.screens.details.DetailsUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RepositoryDetailsViewModel(
    val owner: String,
    val name: String,
    private val getRepoDetailsUseCase: GetRepoDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState<RepoDetails>>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState<RepoDetails>> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh || _uiState.value !is DetailsUiState.Success) {
                _uiState.value = DetailsUiState.Loading
            }
            try {
                when (val result = getRepoDetailsUseCase(owner, name, forceRefresh)) {
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
