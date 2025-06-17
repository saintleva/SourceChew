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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.repository.SearchState
import com.github.saintleva.sourcechew.ui.common.CheckBoxWithText
import com.github.saintleva.sourcechew.ui.screens.found.FoundScreen
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.enter_search_text
import sourcechew.composeapp.generated.resources.search
import sourcechew.composeapp.generated.resources.stop_search
import sourcechew.composeapp.generated.resources.use_previous_search_conditions


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
    val scopeStrings = mapOf(
        RepoSearchScope.NAME to stringResource(Res.string.names),
        RepoSearchScope.DESCRIPTION to stringResource(Res.string.descriptions),
        RepoSearchScope.README to stringResource(Res.string.readme),
    )
    val onlyFlagStrings = mapOf(
        OnlyFlag.PUBLIC to stringResource(Res.string.public_only),
        OnlyFlag.PRIVATE to stringResource(Res.string.private_only),
        OnlyFlag.FORK to stringResource(Res.string.fork_only),
        OnlyFlag.ARCHIVED to stringResource(Res.string.archived_only),
        OnlyFlag.MIRROR to stringResource(Res.string.mirror_only),
        OnlyFlag.TEMPLATE to stringResource(Res.string.template_only),
    )

    Column {
        OutlinedTextField(
            value = screenModel.query.value,
            onValueChange = screenModel::onQueryChange,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            enabled = selectingEnabled,
            textStyle = TextStyle(fontSize = 16.sp),
            label = { Text(stringResource(Res.string.enter_search_text)) },
            isError = screenModel.query.value.isBlank()
        )
        RepoSearchScope.all.forEach { scope ->
            val textStyle = MaterialTheme.typography.labelLarge
            FilterChip(
                selected = screenModel.selectedSearchScope[scope]!!,
                onClick = { screenModel.toggleScope(scope) },
                label = { Text(text = scopeStrings[scope]!!, style = textStyle) },
                enabled = selectingEnabled
            )
        }
        OnlyFlag.all.forEach { flag ->
            CheckBoxWithText(
                text = onlyFlagStrings[flag]!!,
                checked = screenModel.selectedOnlyFlags[flag]!!,
                onCheckedChange = screenModel::usePreviousConditionsSearchChange,
                enabled = selectingEnabled && screenModel.canUsePreviousConditions(),
                padding = 8.dp
            )
        }
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