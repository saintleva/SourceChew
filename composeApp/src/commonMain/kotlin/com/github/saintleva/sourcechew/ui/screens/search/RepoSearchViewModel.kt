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

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoOnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import kotlinx.coroutines.flow.update

class RepoSearchViewModel(
    conditionsStore: ConfigStore<RepoSearchConditions>,
    appSettingsStore: ConfigStore<AppSettings>,
    searchInteractor: SearchInteractor<RepoSearchConditions, FoundRepo>
) : BaseSearchViewModel<RepoSearchConditions, FoundRepo>(
    conditionsStore = conditionsStore,
    appSettingsStore = appSettingsStore,
    searchInteractor = searchInteractor,
    initialConditions = RepoSearchConditions.default,
    usePreviousSearchLens = AppSettings.UsePreviousRepoSearchLens
) {

    fun onSortChange(sort: RepoSearchSort) {
        _conditions.update { it.copy(sort = sort) }
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

    fun toggleOnlyFlag(flag: RepoOnlyFlag) {
        _conditions.update { current ->
            val newFlags = if (flag in current.onlyFlags) {
                current.onlyFlags - flag
            } else {
                current.onlyFlags + flag
            }
            current.copy(onlyFlags = newFlags)
        }
    }
}