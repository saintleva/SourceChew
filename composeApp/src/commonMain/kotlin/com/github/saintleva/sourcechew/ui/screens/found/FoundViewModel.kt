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
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FoundViewModel(private val searchInteractor: RepoSearchInteractor) : ViewModel() {

    init {
        Napier.d(tag = "init") { "FoundViewModel created: ${this.hashCode()} with Interactor: ${searchInteractor.hashCode()}" }
    }

    val searchState = searchInteractor.searchState

    val foundFlow: Flow<PagingData<FoundRepo>> = searchState
        .map { state -> (state as? SearchState.Found)?.flow }
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { it }
        .cachedIn(viewModelScope)

    fun onNavigationBack() {
        searchInteractor.switchToSelecting()
    }
}
