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
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import com.jamal_aliev.paginator.offset.Paginator
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch


class FoundViewModel<ItemSearchConditions, FoundItem: FoundBase>(
    private val searchInteractor: SearchInteractor<ItemSearchConditions, FoundItem>
) : ViewModel() {

    init {
        Napier.d(tag = "init") {
            "FoundViewModel created: ${this.hashCode()} with Interactor: ${searchInteractor.hashCode()}"
        }
    }

    val paginator: Paginator<FoundItem>?
        get() = (searchInteractor.searchState.value as? SearchState.Found)?.paginator

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
