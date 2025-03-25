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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.domain.repository.SearchState
import com.github.saintleva.sourcechew.ui.common.CheckBoxWithText
import com.github.saintleva.sourcechew.ui.screens.found.FoundScreen
import com.github.saintleva.sourcechew.ui.style.forgeIconResources
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.enter_search_text
import sourcechew.composeapp.generated.resources.groups
import sourcechew.composeapp.generated.resources.logo
import sourcechew.composeapp.generated.resources.repositories
import sourcechew.composeapp.generated.resources.search
import sourcechew.composeapp.generated.resources.stop_search
import sourcechew.composeapp.generated.resources.use_previous_search_conditions
import sourcechew.composeapp.generated.resources.users


class SearchScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SearchScreenModel>()
        val navigator = LocalNavigator.currentOrThrow

        val searchState = screenModel.searchState.collectAsStateWithLifecycle()

        if (searchState.value is SearchState.Success) {
            navigator.push(FoundScreen())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SearchContent(screenModel, searchState.value == SearchState.Selecting)
            if (searchState.value == SearchState.Searching) {
                SearchProgress(screenModel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchContent(screenModel: SearchScreenModel, selectingEnabled: Boolean) {
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
                    enabled = selectingEnabled,
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
                label = { Text(stringResource(Res.string.repositories)) },
                enabled = selectingEnabled
            )
            FilterChip(
                selected = screenModel.userOption.value,
                onClick = screenModel::toggleUser,
                label = { Text(stringResource(Res.string.users)) },
                enabled = selectingEnabled
            )
            FilterChip(
                selected = screenModel.groupOption.value,
                onClick = screenModel::toggleGroup,
                label = { Text(stringResource(Res.string.groups)) },
                enabled = selectingEnabled
            )
        }
        OutlinedTextField(
            value = screenModel.text.value,
            onValueChange = screenModel::onTextChange,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            enabled = selectingEnabled,
            textStyle = TextStyle(fontSize = 16.sp),
            label = { Text(stringResource(Res.string.enter_search_text)) },
            isError = screenModel.text.value.isEmpty()
        )
        CheckBoxWithText(
            text = stringResource(Res.string.use_previous_search_conditions),
            checked = screenModel.usePreviousSearch.value,
            onCheckedChange = screenModel::usePreviousConditionsSearchChange,
            enabled = selectingEnabled && screenModel.canUsePreviousConditions(),
            padding = 8.dp
        )
        Button(
            onClick = screenModel::search,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            enabled = selectingEnabled && screenModel.maySearch()
        ) {
            Text(stringResource(Res.string.search))
        }
    }
}

//TODO: Force CircularProgressIndicator to occupy full residuary space of screen if scrolling
// is not using
@Composable
private fun SearchProgress(screenModel: SearchScreenModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        Button(
            onClick = screenModel::stop,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(stringResource(Res.string.stop_search))
        }
    }
}