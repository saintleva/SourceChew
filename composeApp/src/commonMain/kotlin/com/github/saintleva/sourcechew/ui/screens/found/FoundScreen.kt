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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import com.jamal_aliev.paginator.compose.offset.BindToLazyList
import com.jamal_aliev.paginator.compose.offset.rememberPrefetchController
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PageState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.found_items
import sourcechew.composeapp.generated.resources.loading_error
import sourcechew.composeapp.generated.resources.loading_more_error
import sourcechew.composeapp.generated.resources.no_items_found_description
import sourcechew.composeapp.generated.resources.no_items_found_title
import sourcechew.composeapp.generated.resources.retry_button


@Composable
fun FoundScreen(modifier: Modifier, viewModel: FoundViewModel) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val meta by viewModel.metadata.collectAsStateWithLifecycle()
    Napier.d(tag = "FoundScreen") { "uiState = ${ui?.let { it::class.simpleName }}" }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = ui) {
            is PaginatorUiState.Idle,
            is PaginatorUiState.Loading -> FullscreenLoading()

            is PaginatorUiState.Empty -> EmptyState()

            is PaginatorUiState.Error -> ErrorState(
                cause = state.exception,
                onRetry = viewModel::restart
            )

            is PaginatorUiState.Content -> ContentList(
                state = state,
                metadata = meta,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun ContentList(
    state: PaginatorUiState.Content<FoundRepo>,
    metadata: SearchMetadata?,
    viewModel: FoundViewModel
) {
    val paginator = viewModel.paginator ?: return
    val listState = remember(paginator) {
        val initial = viewModel.consumeInitialScroll()
        LazyListState(
            firstVisibleItemIndex = initial?.index ?: 0,
            firstVisibleItemScrollOffset = initial?.offset ?: 0,
        )
    }

    DisposableEffect(paginator) {
        onDispose {
            viewModel.saveScroll(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset,
            )
        }
    }

    val prefetch = paginator.rememberPrefetchController(
        prefetchDistance = PREFETCH_DISTANCE,
        silentlyLoading = false,
    )
    val headerCount = if (metadata != null) 1 else 0
    val footerCount = if (state.appendState != null) 1 else 0

    prefetch.BindToLazyList(
        listState = listState,
        dataItemCount = state.items.size,
        headerCount = headerCount,
        footerCount = footerCount,
    )

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        if (metadata != null) {
            item(key = "metadata-header") { MetadataHeader(metadata) }
        }
        items(state.items, key = { it.id }) { ItemContent(it) }
        state.appendState?.let { appendState ->
            item(key = "append-indicator") {
                Napier.d(tag = "FoundScreen") { "Append indicator must be showed" }
                AppendIndicator(appendState, onRetry = viewModel::loadNext)
            }
        }
    }
}

@Composable
private fun MetadataHeader(metadata: SearchMetadata) {
    Text(
        text = "${stringResource(Res.string.found_items)}: ${metadata.totalCount}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

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
            Text("Owner type: ${foundRepo.ownerType}")
            Text("Description: ${foundRepo.description}")
            Text("Language: ${foundRepo.language}")
            Text("Stars: ${foundRepo.stars}")
        }
    }
}

@Composable
private fun AppendIndicator(
    appendState: PageState<FoundRepo>,
    onRetry: () -> Unit,
) {
    when {
        appendState.isProgressState() -> {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        appendState.isErrorState() -> {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.loading_more_error),
                    color = MaterialTheme.colorScheme.error,
                )
                Button(onClick = onRetry) {
                    Text(stringResource(Res.string.retry_button))
                }
            }
        }

        else -> Unit
    }
}

@Composable
private fun FullscreenLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.no_items_found_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(Res.string.no_items_found_description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun ErrorState(cause: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.loading_error),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            text = getErrorMessage(cause),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(Res.string.retry_button))
        }
    }
}

private const val PREFETCH_DISTANCE = 10
