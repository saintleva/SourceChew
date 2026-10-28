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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.ui.common.AppendIndicator
import com.github.saintleva.sourcechew.ui.common.EmptyContent
import com.github.saintleva.sourcechew.ui.common.ErrorContent
import com.github.saintleva.sourcechew.ui.common.FullscreenLoading
import com.jamal_aliev.paginator.compose.core.PaginatedSlots
import com.jamal_aliev.paginator.compose.offset.PaginatedLazyColumn
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.found_items

@Composable
fun <ItemSearchConditions, FoundItem: FoundBase> FoundScreen(
    modifier: Modifier,
    viewModel: FoundViewModel<ItemSearchConditions, FoundItem>,
    itemContent: @Composable (FoundItem) -> Unit
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

    PaginatedLazyColumn<FoundItem>(
        paginator = paginator,
        modifier = modifier.fillMaxSize(),
        state = listState,
        key = { it.id },
        slots = PaginatedSlots(
            prependErrorIndicator = { state ->
                AppendIndicator(
                    errorState = state,
                    onRetry = viewModel::loadPrevious
                )
            },
            appendErrorIndicator = { state ->
                AppendIndicator(
                    errorState = state,
                    onRetry = viewModel::loadNext
                )
            },
            loadingContent = { FullscreenLoading() },
            emptyContent = { EmptyContent() },
            errorContent = { state ->
                ErrorContent(
                    cause = state.exception,
                    onRetry = viewModel::restart
                )
            }
        )
    ) { item, globalIndex, _, _, page ->
        if (globalIndex == 0) {
            val meta = page.metadata as? SearchMetadata
            meta?.let { MetadataHeader(meta) }
        }
        itemContent(item)
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
