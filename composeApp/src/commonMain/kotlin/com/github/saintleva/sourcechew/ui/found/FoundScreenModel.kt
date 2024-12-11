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

package com.github.saintleva.sourcechew.ui.found

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.saintleva.sourcechew.domain.models.FoundItems
import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.repository.SearchRepository
import kotlinx.coroutines.launch


sealed interface SearchItemsState {
    data object Searching : SearchItemsState
    data class Error(val cause: Throwable) : SearchItemsState
    data class Success(val items: FoundItems) : SearchItemsState
}

class FoundScreenModel(
    private val configRepository: ConfigRepository,
    private val searchRepository: SearchRepository
) : ScreenModel {

    private var _previousConditions: SearchConditions?
        get() = configRepository.previousConditions
        set(value) {
            configRepository.previousConditions = value
        }

    private val _foundItems = mutableStateOf<SearchItemsState>(SearchItemsState.Searching)
    val foundItems: State<SearchItemsState> = _foundItems

    fun find(conditions: SearchConditions) {
        if (conditions != _previousConditions) {
            _foundItems.value = SearchItemsState.Searching
            _previousConditions = conditions
            screenModelScope.launch {
                _foundItems.value = SearchItemsState.Success(searchRepository.search(conditions))
            }

        }
    }
}