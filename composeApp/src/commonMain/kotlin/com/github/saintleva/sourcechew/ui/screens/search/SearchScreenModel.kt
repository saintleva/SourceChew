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

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SearchScreenModel(
    configManager: ConfigManager,
    private val searchRepository: RepoSearchInteractor
) : ScreenModel {

    val accessor = configManager.repoConditions
    val conditionsStateFlows = accessor.flows.toConditionsStateFlow(screenModelScope)

    val searchState = searchRepository.searchState

    private var _searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        screenModelScope.launch {
            accessor.changeQuery(newQuery)
        }
    }

    fun toggleScope(scope: RepoSearchScope) {
        screenModelScope.launch {
            accessor.toggleScopeItem(scope)
        }
    }

    fun toggleOnlyFlag(flag: OnlyFlag) {
        screenModelScope.launch {
            when (flag) {
                OnlyFlag.PUBLIC -> accessor.togglePublicOnlyFlag()
                OnlyFlag.PRIVATE -> accessor.togglePrivateOnlyFlag()
                else -> accessor.toggleOnlyFlag(flag)
            }
        }
    }

    fun onSortChange(sort: RepoSearchSort) {
        screenModelScope.launch {
            accessor.changeSort(sort)
        }
    }

    fun onOrderChange(order: SearchOrder) {
        screenModelScope.launch {
            accessor.changeOrder(order)
        }
    }

    fun usePreviousSearchChange(checked: Boolean) {
        screenModelScope.launch {
            accessor.changeUsePreviousSearch(checked)
        }
    }

    fun canUsePreviousConditions() =
        searchRepository.сanUsePreviousConditions(conditionsStateFlows.toConditions())

    fun search() {
        _searchJob = screenModelScope.launch {
            searchRepository.search(conditionsStateFlows.toConditions(),
                conditionsStateFlows.usePreviousSearch.value)
        }
    }

    fun stop() {
        _searchJob?.cancel()
        searchRepository.switchToSelecting()
    }
}