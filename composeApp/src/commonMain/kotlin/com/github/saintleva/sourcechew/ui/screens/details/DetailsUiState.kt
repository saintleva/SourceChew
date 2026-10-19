package com.github.saintleva.sourcechew.ui.screens.details

import com.github.saintleva.sourcechew.domain.models.DetailsBase

sealed interface DetailsUiState<out T : DetailsBase> {
    data object Loading : DetailsUiState<Nothing>
    data class Success<T : DetailsBase>(val details: T) : DetailsUiState<T>
    data class Error(val cause: Throwable) : DetailsUiState<Nothing>
}
