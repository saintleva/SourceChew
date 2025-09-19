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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import com.github.saintleva.sourcechew.ui.common.NavigableUpScreen
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.found_items


class FoundScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<FoundScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val searchState = screenModel.searchState.collectAsStateWithLifecycle()
        if (searchState.value == SearchState.Selecting) {
            navigator.pop()
        }
        FoundContent(screenModel)
    }
}

@Composable
private fun ItemContent(foundRepo: FoundRepo) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Author: ${foundRepo.author}")
            Text("Name: ${foundRepo.name}")
            Text("Description: ${foundRepo.description}")
            Text("Language: ${foundRepo.language}")
            Text("Start: ${foundRepo.stars}")
        }
    }
}

@Composable
private fun FoundContent(screenModel: FoundScreenModel) {
    val foundRepos = (screenModel.searchState.value as SearchState.Success).items
    NavigableUpScreen(
        title = stringResource(Res.string.found_items),
        navigationUp = screenModel::navigateBack
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            foundRepos.forEach {
                item { ItemContent(it) }
            }
        }
    }
}