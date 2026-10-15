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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.ic_repo
import sourcechew.composeapp.generated.resources.owners
import sourcechew.composeapp.generated.resources.repositories

@Serializable
enum class SearchTab {
    REPO,
    OWNER
}

/**
 * Screen with top TabControl (PrimaryTabRow) to switch between Repo search and User search forms.
 */
@Composable
fun SearchTabScreen(
    modifier: Modifier = Modifier,
    onFoundRepo: () -> Unit = {},
    onFoundOwner: () -> Unit = {},
    initialTab: SearchTab = SearchTab.REPO,
    repoViewModel: RepoSearchViewModel = koinViewModel(),
    ownerViewModel: OwnerSearchViewModel = koinViewModel()
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(initialTab.ordinal) }

    val repoSearchState by repoViewModel.searchState.collectAsStateWithLifecycle()
    val ownerSearchState by ownerViewModel.searchState.collectAsStateWithLifecycle()

    LaunchedEffect(repoSearchState) {
        if (repoSearchState is SearchState.Found) {
            onFoundRepo()
        }
    }

    LaunchedEffect(ownerSearchState) {
        if (ownerSearchState is SearchState.Found) {
            onFoundOwner()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_repo),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(Res.string.repositories))
                }
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(Res.string.owners))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
