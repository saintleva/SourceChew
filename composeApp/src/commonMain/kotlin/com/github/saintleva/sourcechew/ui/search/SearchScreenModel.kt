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
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository


class SearchScreenModel(configRepository: ConfigRepository) : ScreenModel {

    val selectedForges = mutableStateMapOf<Forge, Boolean>()

    private val _repositoryOption = mutableStateOf(configRepository.previousConditions.typeOptions.repository)
    val repositoryOption: State<Boolean> = _repositoryOption

    private val _userOption = mutableStateOf(configRepository.previousConditions.typeOptions.user)
    val userOption: State<Boolean> = _userOption

    private val _groupOption = mutableStateOf(configRepository.previousConditions.typeOptions.group)
    val groupOption: State<Boolean> = _groupOption

    private val _text = mutableStateOf(configRepository.previousConditions.text)
    val text: State<String> = _text

    init {
        for (forge in Forge.list) {
            selectedForges[forge] = configRepository.previousConditions.forgeOptions[forge]!!
        }
    }

    fun maySearch() =
        selectedForges.values.any { it }
                && (_repositoryOption.value || _userOption.value || _groupOption.value)
                && text.value.isNotEmpty()

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

    fun onTextChange(newText: String) {
        _text.value = newText
    }

    fun search() {

    }
}