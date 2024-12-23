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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.ui.style.forgeIconResources
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.enter_search_text
import sourcechew.composeapp.generated.resources.groups
import sourcechew.composeapp.generated.resources.logo
import sourcechew.composeapp.generated.resources.repositories
import sourcechew.composeapp.generated.resources.search
import sourcechew.composeapp.generated.resources.use_previous_conditions_search
import sourcechew.composeapp.generated.resources.users


class SearchScreen() : Screen {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SearchScreenModel>()

        Column {
            FlowRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp,
                    alignment = Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Top
            ) {
                //TODO: Show right Gitlab and Bitbucket logo instead of black rectangle
                Forge.list.forEach { forge ->
                    val iconResource = forgeIconResources[forge]
                    val textStyle = MaterialTheme.typography.labelLarge
                    FilterChip(
                        selected = screenModel.selectedForges[forge]!!,
                        onClick = { screenModel.toggleForge(forge) },
                        label = { Text(text = forge.name, style = textStyle) },
                        leadingIcon = {
                            iconResource?.let {
                                val textSizeDp= with(LocalDensity.current) {
                                    textStyle.fontSize.toDp()
                                }
                                Icon(
                                    painter = painterResource(it),
                                    contentDescription = "${forge.name} ${stringResource(Res.string.logo)}",
                                    modifier = Modifier.size(textSizeDp * 1.75f)
                                )
                            }
                        }
                    )
                }
            }
            FlowRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp,
                    alignment = Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Top
            ) {
                FilterChip(
                    selected = screenModel.repoOption.value,
                    onClick = screenModel::toggleRepository,
                    label = { Text(stringResource(Res.string.repositories)) }
                )
                FilterChip(
                    selected = screenModel.userOption.value,
                    onClick = screenModel::toggleUser,
                    label = { Text(stringResource(Res.string.users)) }
                )
                FilterChip(
                    selected = screenModel.groupOption.value,
                    onClick = screenModel::toggleGroup,
                    label = { Text(stringResource(Res.string.groups)) }
                )
            }
            OutlinedTextField(
                value = screenModel.text.value,
                onValueChange = screenModel::onTextChange,
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                label = { Text(stringResource(Res.string.enter_search_text)) },
                isError = screenModel.text.value.isEmpty()
            )
            Button(
                onClick = { screenModel.search() },
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                enabled = screenModel.maySearch()
            ) {
                Text(stringResource(Res.string.search))
            }
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = screenModel.usePreviousConditionsSearch.value,
                    onCheckedChange = screenModel::usePreviousConditionsSearchChange,
                    enabled = screenModel.conditionsIsPrevious()
                )
                Text(stringResource(Res.string.use_previous_search_conditions))
            }
        }
    }

}