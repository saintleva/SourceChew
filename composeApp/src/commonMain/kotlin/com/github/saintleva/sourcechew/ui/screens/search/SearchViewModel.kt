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

package com.github.saintleva.sourcechew.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractor
import com.github.saintleva.sourcechew.ui.utils.DEBOUNCE
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SearchViewModel(
    private val repoConditionsStore: ConfigStore<RepoSearchConditions>,
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val searchInteractor: RepoSearchInteractor
) : ViewModel() {

    private val _conditions = MutableStateFlow(RepoSearchConditions.default)
    val conditions: StateFlow<RepoSearchConditions> = _conditions.asStateFlow()

    val maySearch: StateFlow<Boolean> = conditions
        .map { it.maySearch() }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = false
        )

    val usePreviousRepoSearch: StateFlow<Boolean> = appSettingsStore.config
        .map { it.usePreviousRepoSearch }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = AppSettings.default.usePreviousRepoSearch
        )

    val searchState = searchInteractor.searchState

    private var _searchJob: Job? = null

    init {
        Napier.d(tag = "init") { "SearchViewModel created: ${this.hashCode()} with Interactor: ${searchInteractor.hashCode()}" }

        viewModelScope.launch {
            _conditions.value = repoConditionsStore.config.first()
            _conditions
                .drop(1)
                .debounce(DEBOUNCE)
                .collect { currentConditions ->
                    repoConditionsStore.update { currentConditions }
                }
        }
    }

    fun onQueryChange(query: String) {
        _conditions.update { it.copy(query = query) }
    }

    fun toggleScope(scope: RepoSearchScope) {
        _conditions.update { current ->
            val newScopes = if (scope in current.inScope) {
                current.inScope - scope
            } else {
                current.inScope + scope
            }
            current.copy(inScope = newScopes)
        }
    }

    fun toggleOnlyFlag(flag: OnlyFlag) {
        _conditions.update { current ->
            val newFlags = if (flag in current.onlyFlags) {
                current.onlyFlags - flag
            } else {
                current.onlyFlags + flag
            }
            current.copy(onlyFlags = newFlags)
        }
    }

    fun onSortChange(sort: RepoSearchSort) {
        _conditions.update { it.copy(sort = sort) }
    }

    fun onOrderChange(order: SearchOrder) {
        _conditions.update { it.copy(order = order) }
    }

    fun usePreviousSearchChange(checked: Boolean) {
        viewModelScope.launch {
            appSettingsStore.update { it.copy(usePreviousRepoSearch = checked) }
        }
    }

    fun canUsePreviousConditions() =
        searchInteractor.canUsePreviousConditions(conditions.value)

    fun search() {
        Napier.d(tag = "init") { "SearchViewModel.search() called with: ${this.hashCode()}" }
        _searchJob = viewModelScope.launch {
            searchInteractor.search(conditions.value, usePreviousRepoSearch.value)
        }
    }

    fun stop() {
        _searchJob?.cancel()
        searchInteractor.switchToSelecting()
    }
}