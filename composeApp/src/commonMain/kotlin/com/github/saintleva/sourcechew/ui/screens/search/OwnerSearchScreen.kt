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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.OwnerSearchScope
import com.github.saintleva.sourcechew.domain.models.OwnerSearchSort
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.ui.common.CheckBoxWithText
import com.github.saintleva.sourcechew.ui.common.ExpandableSection
import com.github.saintleva.sourcechew.ui.common.IntFilterInput
import com.github.saintleva.sourcechew.ui.common.RadioButtonWithText
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.additional_filters
import sourcechew.composeapp.generated.resources.best_match
import sourcechew.composeapp.generated.resources.email
import sourcechew.composeapp.generated.resources.followers
import sourcechew.composeapp.generated.resources.followers_count
import sourcechew.composeapp.generated.resources.fullname
import sourcechew.composeapp.generated.resources.joined_time
import sourcechew.composeapp.generated.resources.location
import sourcechew.composeapp.generated.resources.login
import sourcechew.composeapp.generated.resources.organization
import sourcechew.composeapp.generated.resources.owner_type
import sourcechew.composeapp.generated.resources.repos_count
import sourcechew.composeapp.generated.resources.repositories
import sourcechew.composeapp.generated.resources.search_in
import sourcechew.composeapp.generated.resources.sort_by
import sourcechew.composeapp.generated.resources.user

@Composable
fun OwnerSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: OwnerSearchViewModel,
    onFound: () -> Unit,
) {
    BaseSearchScreen(
        modifier = modifier,
        viewModel = viewModel,
        onFound = onFound,
    ) { conditions, selectingEnabled ->
        OwnerSpecificFilters(
            viewModel = viewModel,
            conditions = conditions,
            selectingEnabled = selectingEnabled,
        )
    }
}

@Composable
private fun OwnerSpecificFilters(
    viewModel: OwnerSearchViewModel,
    conditions: OwnerSearchConditions,
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
            OwnerSearchScope.entries.forEach { scope ->
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
        Text(
            text = stringResource(Res.string.owner_type),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
        OwnerType.entries.forEach { type ->
            CheckBoxWithText(
                text = type.displayText(),
                checked = type in conditions.types,
                onCheckedChange = { viewModel.toggleType(type) },
                enabled = selectingEnabled,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        OutlinedTextField(
            value = conditions.location ?: "",
            onValueChange = { viewModel.onLocationChange(it.ifBlank { null }) },
            label = { Text(stringResource(Res.string.location)) },
            enabled = selectingEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
        IntFilterInput(
            label = stringResource(Res.string.repos_count),
            filter = conditions.repos,
            onFilterChange = { viewModel.onReposFilterChange(it) },
            enabled = selectingEnabled,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
        IntFilterInput(
            label = stringResource(Res.string.followers_count),
            filter = conditions.followers,
            onFilterChange = { viewModel.onFollowersFilterChange(it) },
            enabled = selectingEnabled,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
    ExpandableSection(title = stringResource(Res.string.sort_by)) {
        OwnerSearchSort.entries.forEach { sort ->
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
private fun OwnerSearchScope.displayText(): String = when (this) {
    OwnerSearchScope.LOGIN -> stringResource(Res.string.login)
    OwnerSearchScope.FULLNAME -> stringResource(Res.string.fullname)
    OwnerSearchScope.EMAIL -> stringResource(Res.string.email)
}

@Composable
private fun OwnerType.displayText(): String = when (this) {
    OwnerType.USER -> stringResource(Res.string.user)
    OwnerType.ORGANIZATION -> stringResource(Res.string.organization)
}

@Composable
private fun OwnerSearchSort.displayText(): String = when (this) {
    OwnerSearchSort.BEST_MATCH -> stringResource(Res.string.best_match)
    OwnerSearchSort.FOLLOWERS -> stringResource(Res.string.followers)
    OwnerSearchSort.REPOSITORIES -> stringResource(Res.string.repositories)
    OwnerSearchSort.JOINED -> stringResource(Res.string.joined_time)
}
