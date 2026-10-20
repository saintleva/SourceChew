package com.github.saintleva.sourcechew.ui.screens.details

import com.github.saintleva.sourcechew.domain.models.DetailsBase
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.DetailsError

sealed interface DetailsUiState<out T : DetailsBase> {
    data object Loading : DetailsUiState<Nothing>
    data class Success<T : DetailsBase>(val details: T) : DetailsUiState<T>
    data class Error(val error: DetailsError) : DetailsUiState<Nothing>
    data class InfrastructureError(val cause: AppException) : DetailsUiState<Nothing>
}
