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

package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.repository.SearchRepository
import com.github.saintleva.sourcechew.ui.found.SearchItemsState
import kotlinx.coroutines.flow.MutableStateFlow

class FindUseCaseImpl(
    private val configRepository: ConfigRepository,
    private val searchRepository: SearchRepository,
) {

    suspend operator fun invoke(
        conditions: SearchConditions,
        foundItems: MutableStateFlow<SearchItemsState>
    ) {
        if (conditions != configRepository.previousConditions) {
            foundItems.value = SearchItemsState.Searching
            configRepository.previousConditions = conditions
            foundItems.value = SearchItemsState.Success(searchRepository.search(conditions))
        }
    }
}