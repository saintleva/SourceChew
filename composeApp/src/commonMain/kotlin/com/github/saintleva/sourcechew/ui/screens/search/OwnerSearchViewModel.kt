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

import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.IntFilter
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.OwnerSearchScope
import com.github.saintleva.sourcechew.domain.models.OwnerSearchSort
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.OwnerSearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OwnerSearchViewModel(
    conditionsStore: ConfigStore<OwnerSearchConditions>,
    private val appSettingsStore: ConfigStore<AppSettings>,
    searchInteractor: SearchInteractor<OwnerSearchConditions, FoundOwner>
) : BaseSearchViewModel<OwnerSearchConditions, FoundOwner>(
    conditionsStore = conditionsStore,
    appSettingsStore = appSettingsStore,
    searchInteractor = searchInteractor,
    initialConditions = OwnerSearchConditions.default
) {

    override val usePreviousSearch: StateFlow<Boolean> = appSettingsStore.config
        .map { it.usePreviousOwnerSearch }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = AppSettings.default.usePreviousOwnerSearch
        )

    fun onSortChange(sort: OwnerSearchSort) {
        _conditions.update { it.copy(sort = sort) }
    }

    fun toggleScope(scope: OwnerSearchScope) {
        _conditions.update { current ->
            val newScopes = if (scope in current.inScope) {
                current.inScope - scope
            } else {
                current.inScope + scope
            }
            current.copy(inScope = newScopes)
        }
    }

    fun toggleType(type: OwnerType) {
        _conditions.update { current ->
            val newTypes = if (type in current.types) {
                current.types - type
            } else {
                current.types + type
            }
            current.copy(types = newTypes)
        }
    }

    fun onReposFilterChange(filter: IntFilter?) {
        _conditions.update { it.copy(repos = filter) }
    }

    fun onFollowersFilterChange(filter: IntFilter?) {
        _conditions.update { it.copy(followers = filter) }
    }

    fun onLocationChange(location: String?) {
        _conditions.update { it.copy(location = location) }
    }

    override fun usePreviousSearchChange(checked: Boolean) {
        viewModelScope.launch {
            appSettingsStore.update { it.copy(usePreviousOwnerSearch = checked) }
        }
    }
}
