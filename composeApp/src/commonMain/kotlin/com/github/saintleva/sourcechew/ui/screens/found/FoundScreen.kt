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

package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.ui.common.HandlePagingLoadStates
import com.github.saintleva.sourcechew.ui.common.pagingAppendFooter


@Composable
private fun ItemContent(foundRepo: FoundRepo) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Name: ${foundRepo.name}")
            Text("Full name: ${foundRepo.fullName}")
            Text("Owner login: ${foundRepo.ownerLogin}")
            Text("Owner login: ${foundRepo.ownerType}")
            Text("Description: ${foundRepo.description}")
            Text("Language: ${foundRepo.language}")
            Text("Stars: ${foundRepo.stars}")
        }
    }
}

@Composable
fun FoundScreen(modifier: Modifier, viewModel: FoundViewModel) {
    val foundRepos = viewModel.foundFlow?.collectAsLazyPagingItems()
    HandlePagingLoadStates(
        lazyPagingItems = foundRepos,
        modifier = modifier
    ) {
        if (foundRepos != null) {
            // This @Composable block will be executed when data is available
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = foundRepos.itemCount,
                    key = foundRepos.itemKey { it.id }
                ) { index ->
                    val item = foundRepos[index]
                    if (item != null) {
                        ItemContent(item)
                    }
                }

                // Use the extension function to add the pagination footer
                pagingAppendFooter(lazyPagingItems = foundRepos)
            }
        }
    }
}
