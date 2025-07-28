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

package com.github.saintleva.sourcechew.ui.screens.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.repository.SearchRepository
import com.github.saintleva.sourcechew.domain.usecase.CanUsePreviousConditionsUseCase
import com.github.saintleva.sourcechew.domain.usecase.FindUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SearchScreenModel(
    private val findUseCase: FindUseCase,
    private val canUsePreviousConditionsUseCase: CanUsePreviousConditionsUseCase,
    private val configManager: ConfigManager,
    private val searchRepository: SearchRepository
) : ScreenModel {

    private val _query = mutableStateOf("")
    val query: State<String> = _query

    val previousConditions =
        configManager.previousRepoConditions.toConditionsStateFlow(screenModelScope)

    val searchState = searchRepository.searchState

    private var _searchJob: Job? = null

    fun maySearch(): Boolean {

        val allPrivacySelected =
            previousConditions.onlyFlags[OnlyFlag.PUBLIC]!!.value && selectedOnlyFlags[OnlyFlag.PRIVATE]!!

        return selectedSearchScope.isNotEmpty() && !allPrivacySelected && query.value.isNotBlank()
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun toggleScope(scope: RepoSearchScope) {
        selectedSearchScope[scope] = !selectedSearchScope[scope]!!
    }

    fun toggleFlag(flag: OnlyFlag) {
        selectedOnlyFlags[flag] = !selectedOnlyFlags[flag]!!
    }

    fun usePreviousConditionsSearchChange(checked: Boolean) {
        screenModelScope.launch {
            configRepository.changeUsePreviousRepoSearch(checked)
            _usePreviousSearch.value = checked
        }
    }

    private fun obtainConditions() = RepoSearchConditions(
        query.value,
        selectedSearchScope.filter { it.value }.keys.toSet(),
        selectedOnlyFlags.filter { it.value }.keys.toSet()
    )

    fun canUsePreviousConditions(): Boolean {
        var result = false
        screenModelScope.launch {
            result = canUsePreviousConditionsUseCase(obtainConditions())
        }
        return result
    }

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