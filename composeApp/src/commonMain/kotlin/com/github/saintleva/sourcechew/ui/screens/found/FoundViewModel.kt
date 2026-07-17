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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import com.jamal_aliev.paginator.core.extension.asUiState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.offset.Paginator
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class FoundViewModel<ItemSearchConditions, FoundItem: FoundBase>(
    private val searchInteractor: SearchInteractor<ItemSearchConditions, FoundItem>
) : ViewModel() {

    init {
        Napier.d(tag = "init") {
            "FoundViewModel created: ${this.hashCode()} with Interactor: ${searchInteractor.hashCode()}"
        }
    }

    val paginator: Paginator<FoundItem>?
        get() = (searchInteractor.searchState.value as? SearchState.Found)?.paginator as Paginator<FoundItem>?

    val metadata: StateFlow<SearchMetadata?> = searchInteractor.searchState
        .flatMapLatest { state ->
            val p = (state as? SearchState.Found)?.paginator
            if (p != null) {
                p.core.snapshot.map { snapshot ->
                    snapshot.firstNotNullOfOrNull { it.metadata as? SearchMetadata }
                }
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = null
        )

    //TODO: Remove this
//    val uiState: StateFlow<PaginatorUiState<FoundItem>> = searchInteractor.searchState
//        .flatMapLatest { state ->
//            val p = (state as? SearchState.Found)?.paginator
//            if (p != null) {
//                p.core.snapshot.asUiState { p.core.isStarted }
//            } else {
//                // Return Idle state if no paginator is currently active
//                flowOf(PaginatorUiState.Idle)
//            }
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = WhileUiSubscribed,
//            initialValue = PaginatorUiState.Idle
//        )

    fun consumeInitialScroll(): ScrollPosition? = searchInteractor.lastScrollPosition

    fun saveScroll(index: Int, offset: Int) {
        searchInteractor.lastScrollPosition = ScrollPosition(index, offset)
    }

    fun restart() {
        viewModelScope.launch { paginator?.restart() }
    }

    fun loadNext() {
        viewModelScope.launch { paginator?.goNextPage() }
    }

    fun onNavigationBack() {
        searchInteractor.switchToSelecting()
    }
}
