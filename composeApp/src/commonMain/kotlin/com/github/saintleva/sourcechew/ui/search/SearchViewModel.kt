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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository


class SearchViewModel(configRepository: ConfigRepository) : ViewModel() {

    val selectedForges = mutableStateMapOf<Forge, Boolean>()

//    private val _selectedForges: Map<Forge, MutableState<Boolean>>
//    val selectedForges: Map<Forge, State<Boolean>>

    private val _repositoryOption = mutableStateOf(configRepository.previousOptions.typeOptions.repository)
    val repositoryOption: State<Boolean> = _repositoryOption

    private val _userOption = mutableStateOf(configRepository.previousOptions.typeOptions.user)
    val userOption: State<Boolean> = _userOption

    private val _groupOption = mutableStateOf(configRepository.previousOptions.typeOptions.group)
    val groupOption: State<Boolean> = _groupOption

    init {
        for (forge in Forge.list) {
            selectedForges[forge] = configRepository.previousOptions.forgeOptions[forge]!!
        }
//        _selectedForges = mutableMapOf()
//        selectedForges = mutableMapOf()
//        for (forge in Forge.list) {
//            _selectedForges[forge] = mutableStateOf(configRepository.previousOptions.forgeOptions[forge]!!)
//            selectedForges[forge] = _selectedForges[forge]!!
//        }
    }

    fun maySearch() =
        selectedForges.values.any { it }
                && (_repositoryOption.value || _userOption.value || _groupOption.value)

    fun toggleForge(forge: Forge) {
        selectedForges[forge] = !selectedForges[forge]!!
    }

    fun toggleRepository() {
        _repositoryOption.value = !_repositoryOption.value
    }

    fun toggleUser() {
        _userOption.value = !_userOption.value
    }

    fun toggleGroup() {
        _groupOption.value = !_groupOption.value
    }
}