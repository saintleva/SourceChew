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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.RepoOnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.ui.common.CheckBoxWithText
import com.github.saintleva.sourcechew.ui.common.ExpandableSection
import com.github.saintleva.sourcechew.ui.common.RadioButtonWithText
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.additional_filters
import sourcechew.composeapp.generated.resources.archived_only
import sourcechew.composeapp.generated.resources.best_match
import sourcechew.composeapp.generated.resources.descriptions
import sourcechew.composeapp.generated.resources.fork_only
import sourcechew.composeapp.generated.resources.forks
import sourcechew.composeapp.generated.resources.mirror_only
import sourcechew.composeapp.generated.resources.names
import sourcechew.composeapp.generated.resources.private_only
import sourcechew.composeapp.generated.resources.public_only
import sourcechew.composeapp.generated.resources.readme
import sourcechew.composeapp.generated.resources.search_in
import sourcechew.composeapp.generated.resources.sort_by
import sourcechew.composeapp.generated.resources.stars
import sourcechew.composeapp.generated.resources.template_only
import sourcechew.composeapp.generated.resources.updated_time

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.usecase.SearchState

@Composable
fun RepoSearchForm(
    modifier: Modifier = Modifier,
    viewModel: RepoSearchViewModel,
    onSearchClick: (() -> Unit)? = null,
) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    BaseSearchContent(
        viewModel = viewModel,
        selectingEnabled = searchState != SearchState.Searching,
        onSearchClick = onSearchClick,
        specificFilters = { conditions, selectingEnabled ->
            RepoSpecificFilters(
                viewModel = viewModel,
                conditions = conditions,
                selectingEnabled = selectingEnabled,
            )
        }
    )
}

@Composable
fun RepoSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: RepoSearchViewModel,
    onFound: () -> Unit,
) {
    BaseSearchScreen(
        modifier = modifier,
        viewModel = viewModel,
        onFound = onFound,
    ) { conditions, selectingEnabled ->
        RepoSpecificFilters(
            viewModel = viewModel,
            conditions = conditions,
            selectingEnabled = selectingEnabled,
        )
    }
}

@Composable
private fun RepoSpecificFilters(
    viewModel: RepoSearchViewModel,
    conditions: RepoSearchConditions,
    selectingEnabled: Boolean,
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(
            text = stringResource(Res.string.search_in),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                4.dp,
                alignment = Alignment.CenterHorizontally,
            ),
        ) {
            RepoSearchScope.entries.forEach { scope ->
                val textStyle = MaterialTheme.typography.labelLarge
                FilterChip(
                    selected = scope in conditions.inScope,
                    onClick = { viewModel.toggleScope(scope) },
                    label = { Text(text = scope.displayText(), style = textStyle) },
                    enabled = selectingEnabled,
                )
            }
        }
    }
    ExpandableSection(title = stringResource(Res.string.additional_filters)) {
        RepoOnlyFlag.entries.forEach { flag ->
            CheckBoxWithText(
                text = flag.displayText(),
                checked = flag in conditions.onlyFlags,
                onCheckedChange = { viewModel.toggleOnlyFlag(flag) },
                enabled = selectingEnabled,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
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
private fun RepoOnlyFlag.displayText(): String = when (this) {
    RepoOnlyFlag.PUBLIC -> stringResource(Res.string.public_only)
    RepoOnlyFlag.PRIVATE -> stringResource(Res.string.private_only)
    RepoOnlyFlag.FORK -> stringResource(Res.string.fork_only)
    RepoOnlyFlag.ARCHIVED -> stringResource(Res.string.archived_only)
    RepoOnlyFlag.MIRROR -> stringResource(Res.string.mirror_only)
    RepoOnlyFlag.TEMPLATE -> stringResource(Res.string.template_only)
}

@Composable
private fun RepoSearchSort.displayText(): String = when (this) {
    RepoSearchSort.BEST_MATCH -> stringResource(Res.string.best_match)
    RepoSearchSort.STARS -> stringResource(Res.string.stars)
    RepoSearchSort.FORKS -> stringResource(Res.string.forks)
    RepoSearchSort.UPDATED -> stringResource(Res.string.updated_time)
}
