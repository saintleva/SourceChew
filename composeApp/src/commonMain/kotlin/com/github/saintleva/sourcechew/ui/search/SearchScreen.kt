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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.saintleva.sourcechew.domain.models.Forge
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {

    Column {
        Row(modifier = Modifier.padding(10.dp)) {
            Forge.list.forEach { forge ->
                FilterChip(
                    selected = viewModel.selectedForges[forge]!!,
                    onClick = { viewModel.toggleForge(forge) },
                    label = { Text(text = forge.name) }
                )
            }
        }
        Row(modifier = Modifier.padding(10.dp)) {
            FilterChip(
                selected = viewModel.repositoryOption.value,
                onClick = { viewModel.toggleRepository() },
                label = { Text("Repositories") }
            )
            FilterChip(
                selected = viewModel.userOption.value,
                onClick = { viewModel.toggleUser() },
                label = { Text("Users") }
            )
            FilterChip(
                selected = viewModel.groupOption.value,
                onClick = { viewModel.toggleGroup() },
                label = { Text("Groups") }
            )
        }
        OutlinedTextField(
            value = viewModel.text.value,
            onValueChange = { viewModel.onTextChange(it) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 16.sp),
            label = { Text("Enter search text") },
//            label = { Text(stringResource(R.string.enter_search_text)) },
            isError = viewModel.text.value.isEmpty()
        )
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.maySearch()
        ) {
            Text("Search")
        }
    }
}