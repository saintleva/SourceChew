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

import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.jamal_aliev.paginator.offset.Paginator
import kotlinx.coroutines.flow.StateFlow


sealed interface SearchState<out FoundItem: FoundBase> {
    data object Selecting : SearchState<Nothing>
    data object Searching : SearchState<Nothing>
    data class Found<out FoundItem: FoundBase>(
        val paginator: Paginator<out FoundItem>
    ) : SearchState<FoundItem>

}

data class ScrollPosition(val index: Int, val offset: Int)

interface SearchInteractor<ItemSearchConditions, out FoundItem: FoundBase> {

    val searchState: StateFlow<SearchState<FoundItem>>

    val everSearched: Boolean

    var lastScrollPosition: ScrollPosition?

    suspend fun search(conditions: ItemSearchConditions, usePreviousSearch: Boolean)

    fun canUsePreviousConditions(newConditions: ItemSearchConditions): Boolean

    fun switchToSelecting()

    fun clear()
}
