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

package com.github.saintleva.sourcechew.ui.found

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.saintleva.sourcechew.domain.repository.SearchState


class FoundScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<FoundScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val searchState = screenModel.searchState.collectAsStateWithLifecycle()
        if (searchState.value == SearchState.Selecting) {
            navigator.pop()
        }
    }
}

@Composable
private fun FoundContent(screenModel: FoundScreenModel) {
    val foundItems = (screenModel.searchState.value as SearchState.Success).items
}