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

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.models.TypeOptions
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.repository.SearchRepository
import com.github.saintleva.sourcechew.domain.usecase.CanUsePreviousConditionsUseCase
import com.github.saintleva.sourcechew.domain.usecase.FindUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SearchScreenModel(
    private val findUseCase: FindUseCase,
    private val canUsePreviousConditionsUseCase: CanUsePreviousConditionsUseCase,
    private val configRepository: ConfigRepository,
    private val searchRepository: SearchRepository
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

    private val _usePreviousSearch =
        mutableStateOf(configRepository.usePreviousSearch)
    val usePreviousSearch: State<Boolean> = _usePreviousSearch

    val searchState = searchRepository.searchState

    private var _searchJob: Job? = null

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
        screenModelScope.launch {
            configRepository.changeUsePreviousSearch(checked)
            _usePreviousSearch.value = checked
        }
    }

    private fun obtainConditions() = SearchConditions(
        selectedForges.toMap(),
        TypeOptions(repoOption.value, userOption.value, groupOption.value),
        text.value
    )

    fun canUsePreviousConditions() = canUsePreviousConditionsUseCase(obtainConditions())

    fun search() {
        _searchJob = screenModelScope.launch {
            findUseCase(obtainConditions())
        }
    }

    fun stop() {
        _searchJob?.cancel()
        searchRepository.switchToSelecting()
    }
}