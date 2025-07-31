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

package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.FoundRepos
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import kotlinx.coroutines.flow.StateFlow


sealed interface SearchState {
    data object Selecting : SearchState
    data object Searching : SearchState
    data class Error(val cause: Throwable) : SearchState
    data class Success(val items: List<FoundRepo>) : SearchState
}

interface SearchRepository {

    val searchState: StateFlow<SearchState>

    var previousConditions: RepoSearchConditions?

    var previousResult: FoundRepos?

    val everSearched: Boolean
        get() = (previousResult != null)

    suspend fun search(conditions: RepoSearchConditions, usePreviousSearch: Boolean)

    fun switchToSelecting()
}