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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.Forge
import org.jetbrains.skia.FilterMode
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.padding(10.dp)) {
            Forge.list.forEach { forge ->
                FilterChip(
                    selected = viewModel.selectedForges[forge]!!,
                    onClick = { viewModel.toggleForge(forge) },
                ) {
                    Text(text = forge.name)
                }
            }
        }
        Row(modifier = Modifier.padding(10.dp)) {
            FilterChip(
                selected = viewModel.repositoryOption.value,
                onClick = { viewModel.toggleRepository() },
            ) {
                Text("Repositories")
            }
            FilterChip(
                selected = viewModel.userOption.value,
                onClick = { viewModel.toggleUser() },
            ) {
                Text("Users")
            }
            FilterChip(
                selected = viewModel.groupOption.value,
                onClick = { viewModel.toggleGroup() },
            ) {
                Text("Groups")
            }
        }
    }
}