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

package com.github.saintleva.sourcechew.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.domain.models.FoundItems
import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.models.TypeOptions
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.usecase.FindUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


sealed interface SearchItemsState {
    data object Searching : SearchItemsState
    data class Error(val cause: Throwable) : SearchItemsState
    data class Success(val items: FoundItems) : SearchItemsState
}

sealed interface NavigationEvent {
    object NavigateToFoundScreen: NavigationEvent
    object NavigateBack: NavigationEvent
}

class SearchScreenModel(
    private val findUseCase: FindUseCase,
    private val configRepository: ConfigRepository
) : ScreenModel {

    val selectedForges = mutableStateMapOf<Forge, Boolean>()

    private val _repoOption = mutableStateOf(configRepository.previousConditions.typeOptions.repo)
    val repoOption: State<Boolean> = _repoOption

    private val _userOption = mutableStateOf(configRepository.previousConditions.typeOptions.user)
    val userOption: State<Boolean> = _userOption

    private val _groupOption = mutableStateOf(configRepository.previousConditions.typeOptions.group)
    val groupOption: State<Boolean> = _groupOption

    private val _text = mutableStateOf(configRepository.previousConditions.text)
    val text: State<String> = _text

    private val _usePreviousConditionsSearch =
        mutableStateOf(configRepository.usePreviousConditionsSearch)
    val usePreviousConditionsSearch: State<Boolean> = _usePreviousConditionsSearch

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        for (forge in Forge.list) {
            selectedForges[forge] = configRepository.previousConditions.forgeOptions[forge]!!
        }
    }

    fun maySearch() =
        selectedForges.values.any { it }
                && (_repoOption.value || _userOption.value || _groupOption.value)
                && text.value.isNotEmpty()

    fun toggleForge(forge: Forge) {
        selectedForges[forge] = !selectedForges[forge]!!
    }

    fun toggleRepository() {
        _repoOption.value = !_repoOption.value
    }

    fun toggleUser() {
        _userOption.value = !_userOption.value
    }

    fun toggleGroup() {
        _groupOption.value = !_groupOption.value
    }

    fun onTextChange(newText: String) {
        _text.value = newText
    }

    fun usePreviousConditionsSearchChange(checked: Boolean) {
        _usePreviousConditionsSearch.value = checked
        configRepository.usePreviousConditionsSearch = checked
    }

    fun obtainConditions() = SearchConditions(
        selectedForges.toMap(),
        TypeOptions(repoOption.value, userOption.value, groupOption.value),
        text.value
    )

    fun conditionsIsPrevious() = obtainConditions() == configRepository.previousConditions

    fun search() {
        screenModelScope.launch {
            findUseCase(obtainConditions())
            _navigationEvents.emit(NavigationEvent.NavigateToFoundScreen)
        }
    }
}