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

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import com.jamal_aliev.paginator.extension.asUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.onStart

class FoundViewModel(private val searchInteractor: RepoSearchInteractor) : ViewModel() {

    init {
        Napier.d(tag = "init") { "FoundViewModel created: ${this.hashCode()} with Interactor: ${searchInteractor.hashCode()}" }
    }

    val paginator = (searchInteractor.searchState as? SearchState.Found)?.paginator

    private val _metadata = mutableStateOf<SearchMetadata?>(null)
    val metadata: State<SearchMetadata?> = _metadata

    val uiState = paginator?.core?.snapshot
        ?.onStart { _metadata.value = TODO() }
        ?.asUiState { paginator.core.isStarted }

    fun onNavigationBack() {
        searchInteractor.switchToSelecting()
    }
}
