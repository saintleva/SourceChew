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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import com.github.saintleva.sourcechew.ui.common.CheckBoxWithText
import com.github.saintleva.sourcechew.ui.common.ExpandableSection
import com.github.saintleva.sourcechew.ui.common.RadioButtonWithText
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.additional_filters
import sourcechew.composeapp.generated.resources.archived_only
import sourcechew.composeapp.generated.resources.ascending
import sourcechew.composeapp.generated.resources.best_match
import sourcechew.composeapp.generated.resources.descending
import sourcechew.composeapp.generated.resources.descriptions
import sourcechew.composeapp.generated.resources.enter_search_text
import sourcechew.composeapp.generated.resources.fork_only
import sourcechew.composeapp.generated.resources.forks
import sourcechew.composeapp.generated.resources.mirror_only
import sourcechew.composeapp.generated.resources.names
import sourcechew.composeapp.generated.resources.order
import sourcechew.composeapp.generated.resources.private_only
import sourcechew.composeapp.generated.resources.public_only
import sourcechew.composeapp.generated.resources.readme
import sourcechew.composeapp.generated.resources.search
import sourcechew.composeapp.generated.resources.search_in
import sourcechew.composeapp.generated.resources.sort_by
import sourcechew.composeapp.generated.resources.stars
import sourcechew.composeapp.generated.resources.stop_search
import sourcechew.composeapp.generated.resources.template_only
import sourcechew.composeapp.generated.resources.updated_time
import sourcechew.composeapp.generated.resources.use_previous_search_conditions

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onFound: () -> Unit
) {
    val searchState = viewModel.searchState.collectAsStateWithLifecycle()

    LaunchedEffect(searchState.value) {
        if (searchState.value is SearchState.Found) {
            onFound()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            //TODO: Make good top padding
            //.padding(WindowInsets.safeContent.asPaddingValues())
            .verticalScroll(rememberScrollState())
    ) {
        SearchContent(viewModel, searchState.value != SearchState.Searching)
        if (searchState.value == SearchState.Searching) {
            SearchProgress(viewModel)
        }
    }
}


@Composable
private fun SearchContent(viewModel: SearchViewModel, selectingEnabled: Boolean) {

    val conditions by viewModel.conditions.collectAsStateWithLifecycle()
    val usePreviousRepoSearch by viewModel.usePreviousRepoSearch.collectAsStateWithLifecycle()
    val maySearch by viewModel.maySearch.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = conditions.query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            enabled = selectingEnabled,
            textStyle = TextStyle(fontSize = 16.sp),
            label = { Text(stringResource(Res.string.enter_search_text)) },
            isError = conditions.query.isBlank()
        )
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(Res.string.search_in),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp,
                    alignment = Alignment.CenterHorizontally)
            ) {
                RepoSearchScope.entries.forEach { scope ->
                    val textStyle = MaterialTheme.typography.labelLarge
                    FilterChip(
                        selected = scope in conditions.inScope,
                        onClick = { viewModel.toggleScope(scope) },
                        label = { Text(text = scope.displayText(), style = textStyle) },
                        enabled = selectingEnabled
                    )
                }
            }
        }
        ExpandableSection(title = stringResource(Res.string.additional_filters)) {
            OnlyFlag.entries.forEach { flag ->
                CheckBoxWithText(
                    text = flag.displayText(),
                    checked = flag in conditions.onlyFlags,
                    onCheckedChange = { viewModel.toggleOnlyFlag(flag) },
                    enabled = selectingEnabled,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        ExpandableSection(title = stringResource(Res.string.sort_by)) {
            RepoSearchSort.entries.forEach { sort ->
                RadioButtonWithText(
                    text = sort.displayText(),
                    selected = conditions.sort == sort,
                    onClick = { viewModel.onSortChange(sort) },
                    enabled = selectingEnabled,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        ExpandableSection(title = stringResource(Res.string.order)) {
            SearchOrder.entries.forEach { order ->
                RadioButtonWithText(
                    text = order.displayText(),
                    selected = conditions.order == order,
                    onClick = { viewModel.onOrderChange(order) },
                    enabled = selectingEnabled,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        CheckBoxWithText(
            text = stringResource(Res.string.use_previous_search_conditions),
            checked = usePreviousRepoSearch,
            onCheckedChange = viewModel::usePreviousSearchChange,
            enabled = selectingEnabled && viewModel.canUsePreviousConditions(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Button(
            onClick = viewModel::search,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            enabled = selectingEnabled && maySearch
        ) {
            Text(stringResource(Res.string.search))
        }
    }
}

//TODO: Force CircularProgressIndicator to occupy full residuary space of screen if scrolling
// is not using
@Composable
private fun SearchProgress(viewModel: SearchViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        Button(
            onClick = viewModel::stop,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(stringResource(Res.string.stop_search))
        }
    }
}

@Composable
private fun RepoSearchScope.displayText(): String = when (this) {
    RepoSearchScope.NAME -> stringResource(Res.string.names)
    RepoSearchScope.DESCRIPTION -> stringResource(Res.string.descriptions)
    RepoSearchScope.README -> stringResource(Res.string.readme)
}

@Composable
private fun OnlyFlag.displayText(): String = when (this) {
    OnlyFlag.PUBLIC -> stringResource(Res.string.public_only)
    OnlyFlag.PRIVATE -> stringResource(Res.string.private_only)
    OnlyFlag.FORK -> stringResource(Res.string.fork_only)
    OnlyFlag.ARCHIVED -> stringResource(Res.string.archived_only)
    OnlyFlag.MIRROR -> stringResource(Res.string.mirror_only)
    OnlyFlag.TEMPLATE -> stringResource(Res.string.template_only)
}

@Composable
private fun RepoSearchSort.displayText(): String = when (this) {
    RepoSearchSort.BEST_MATCH -> stringResource(Res.string.best_match)
    RepoSearchSort.STARS -> stringResource(Res.string.stars)
    RepoSearchSort.FORKS -> stringResource(Res.string.forks)
    RepoSearchSort.UPDATED -> stringResource(Res.string.updated_time)
}

@Composable
private fun SearchOrder.displayText(): String = when (this) {
    SearchOrder.ASCENDING -> stringResource(Res.string.ascending)
    SearchOrder.DESCENDING -> stringResource(Res.string.descending)
}
