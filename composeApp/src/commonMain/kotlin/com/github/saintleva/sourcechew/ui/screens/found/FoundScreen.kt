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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import io.github.aakira.napier.Napier


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
    Napier.d(tag = "FoundScreen") { "viewModel.uiState = ${viewModel.uiState}" }

    val paginator = viewModel.paginator
    if (paginator != null) {
        val listState = rememberLazyListState()

        val prefetch = paginator.rememberPrefetchController(
            prefetchDistance = 10,
            enableBackwardPrefetch = true,
        )

        prefetch.BindToLazyList(
            listState = listState,
            dataItemCount = uiState.items.size,
            headerCount = 1,
            footerCount = 1,
        )

        LazyColumn(state = listState) {
            item { Header() }
            items(uiState.items, key = { it.id }) { Row(it) }
            item { AppendIndicator(uiState.appendState) }
        }
    }
}
