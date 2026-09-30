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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.repositories
import sourcechew.composeapp.generated.resources.search
import sourcechew.composeapp.generated.resources.users

enum class SearchTab {
    REPO,
    OWNER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    onDismissRequest: () -> Unit,
    onFoundRepo: () -> Unit,
    onFoundOwner: () -> Unit,
    initialTab: SearchTab = SearchTab.REPO,
    repoViewModel: RepoSearchViewModel = koinViewModel(),
    ownerViewModel: OwnerSearchViewModel = koinViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(initialTab.ordinal) }

    val repoSearchState by repoViewModel.searchState.collectAsStateWithLifecycle()
    val ownerSearchState by ownerViewModel.searchState.collectAsStateWithLifecycle()

    LaunchedEffect(repoSearchState) {
        if (repoSearchState is SearchState.Found) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                onDismissRequest()
                onFoundRepo()
            }
        }
    }

    LaunchedEffect(ownerSearchState) {
        if (ownerSearchState is SearchState.Found) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                onDismissRequest()
                onFoundOwner()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(Res.string.search),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
            )

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text(stringResource(Res.string.repositories)) },
                    icon = { Icon(Icons.Default.Code, contentDescription = null) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text(stringResource(Res.string.users)) },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> RepoSearchForm(
                        viewModel = repoViewModel,
                        onSearchClick = {
                            repoViewModel.search()
                        }
                    )
                    1 -> OwnerSearchForm(
                        viewModel = ownerViewModel,
                        onSearchClick = {
                            ownerViewModel.search()
                        }
                    )
                }
            }
        }
    }
}
