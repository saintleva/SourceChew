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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.ui.style.forgeIconResources
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {

    Column {
        FlowRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Forge.list.forEach { forge ->
                val iconResource = forgeIconResources[forge]
                val textStyle = MaterialTheme.typography.labelLarge
                FilterChip(
                    selected = viewModel.selectedForges[forge]!!,
                    onClick = { viewModel.toggleForge(forge) },
                    label = { Text(text = forge.name, style = textStyle) },
                    leadingIcon = {
                        if (iconResource == null)
                            null
                        else {
                            val textSizeDp= with(LocalDensity.current) { textStyle.fontSize.toDp() }
                            Icon(
                                painter = painterResource(iconResource),
                                contentDescription = "${forge.name} logo",
                                modifier = Modifier.size(textSizeDp * 1.75f)
                            )
                        }
                    }
                )
            }
        }
        FlowRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.Top
        ) {
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
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            textStyle = TextStyle(fontSize = 16.sp),
            label = { Text("Enter search text") },
//            label = { Text(stringResource(R.string.enter_search_text)) },
            isError = viewModel.text.value.isEmpty()
        )
        Button(
            onClick = {},
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            enabled = viewModel.maySearch()
        ) {
            Text("Search")
        }
    }
}