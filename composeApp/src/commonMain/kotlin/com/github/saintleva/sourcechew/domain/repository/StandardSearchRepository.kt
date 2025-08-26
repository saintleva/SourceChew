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

import app.cash.paging.PagingData
import com.github.saintleva.sourcechew.domain.NeverSearchedException
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


abstract class StandardSearchRepository : SearchRepository {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Selecting)
    final override val searchState = _searchState.asStateFlow()

    final override var previousConditions: RepoSearchConditions? = null
    final override var previousResult: Flow<PagingData<FoundRepo>>? = null

    protected abstract suspend fun find(conditions: RepoSearchConditions): Flow<PagingData<FoundRepo>>

    final override suspend fun search(conditions: RepoSearchConditions, usePreviousSearch: Boolean) {
        _searchState.update { SearchState.Searching }
        if (conditions == previousConditions) {
            if (usePreviousSearch) {
                usePreviousResult()
            } else {
                obtainNewResult(conditions)
            }
        } else {
            previousConditions = conditions
            obtainNewResult(conditions)
        }
    }

    final override fun сanUsePreviousConditions(newConditions: RepoSearchConditions) =
        everSearched && (newConditions == previousConditions)

    final override fun switchToSelecting() {
        _searchState.update { SearchState.Selecting }
    }

    private suspend fun obtainNewResult(conditions: RepoSearchConditions) {
        val result = find(conditions)
        previousResult = result
        _searchState.update { SearchState.Success(result) }
    }

    private fun usePreviousResult() {
        if (previousResult == null) {
            _searchState.update { SearchState.Error(NeverSearchedException()) }
        } else {
            _searchState.update { SearchState.Success(previousResult!!) }
        }
    }
}