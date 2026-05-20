/*
 * Copyright (C) Anton Liaukevich 2021-2022 <leva.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import com.jamal_aliev.paginator.Paginator
import com.jamal_aliev.paginator.extension.asUiState
import com.jamal_aliev.paginator.page.PaginatorUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class FoundViewModel(
    private val searchInteractor: RepoSearchInteractor
) : ViewModel() {

    init {
        Napier.d(tag = "init") {
            "FoundViewModel created: ${this.hashCode()} with Interactor: ${searchInteractor.hashCode()}"
        }
    }


    val paginator: Paginator<FoundRepo>?
        get() = (searchInteractor.searchState.value as? SearchState.Found)?.paginator

    private val _metadata = mutableStateOf<SearchMetadata?>(null)
    val metadata: State<SearchMetadata?> = _metadata

    val uiState: StateFlow<PaginatorUiState<FoundRepo>> = searchInteractor.searchState
        .flatMapLatest { state ->
            val p = (state as? SearchState.Found)?.paginator
            if (p != null) {
                p.core.snapshot
                    .onEach { snapshot ->
                        // Extract SearchMetadata from any of the loaded pages in the snapshot
                        snapshot.firstNotNullOfOrNull { it.metadata as? SearchMetadata }?.let {
                            _metadata.value = it
                        }
                    }
                    .asUiState { p.core.isStarted }
            } else {
                // Return Idle state if no paginator is currently active
                flowOf(PaginatorUiState.Idle)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = PaginatorUiState.Idle
        )
    fun restart() {
        viewModelScope.launch { paginator?.restart() }
    }

    fun loadNext() {
        viewModelScope.launch { paginator?.goNextPage() }
    }

    fun onNavigationBack() {
        searchInteractor.switchToSelecting()
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
