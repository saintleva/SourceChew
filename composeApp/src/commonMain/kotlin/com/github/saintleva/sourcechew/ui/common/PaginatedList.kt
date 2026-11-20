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

package com.github.saintleva.sourcechew.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.github.saintleva.sourcechew.ui.models.ScrollPosition
import com.github.saintleva.sourcechew.ui.screens.PaginatedViewModel
import com.jamal_aliev.paginator.compose.core.PaginatedSlots
import com.jamal_aliev.paginator.compose.offset.PaginatedLazyColumn
import com.jamal_aliev.paginator.core.page.PageState
import com.jamal_aliev.paginator.offset.Paginator
import kotlinx.coroutines.flow.first

@Composable
fun <Item : Any> PaginatedList(
    viewModel: PaginatedViewModel<Item>,
    key: (Item) -> Any,
    modifier: Modifier = Modifier,
    paginator: Paginator<Item>? = viewModel.paginator,
    slots: PaginatedSlots<Item>,
    itemContent: @Composable LazyItemScope.(item: Item, globalIndex: Int, pageIndex: Int, items: List<Item>, page: PageState<Item>) -> Unit
) {
    if (paginator == null) {
        FullscreenLoading(modifier = modifier)
        return
    }

    val initialScroll = remember(paginator) {
        viewModel.consumeInitialScroll()
    }
    val listState = rememberPaginatedListState(
        paginator = paginator,
        initialScroll = ScrollPosition(initialScroll?.index ?: 0, initialScroll?.offset ?: 0),
        onSaveScroll = viewModel::saveScroll
    )

    PaginatedLazyColumn(
        paginator = paginator,
        modifier = modifier.fillMaxSize(),
        state = listState,
        key = key,
        slots = slots,
        itemContent = itemContent
    )
}

/**
 * Creates and remembers a [LazyListState] bound to a [Paginator], handling scroll restoration
 * and recording scroll positions when the user scrolls or when the composable leaves composition.
 */
@Composable
fun rememberPaginatedListState(
    paginator: Paginator<*>,
    initialScroll: ScrollPosition = ScrollPosition.Zero,
    onSaveScroll: (position: ScrollPosition) -> Unit
): LazyListState {
    val listState = remember(paginator) {
        LazyListState(
            firstVisibleItemIndex = initialScroll.index,
            firstVisibleItemScrollOffset = initialScroll.offset,
        )
    }

    // Continuously record scroll position while user scrolls
    LaunchedEffect(listState) {
        snapshotFlow {
            ScrollPosition(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }.collect { position ->
            if (!position.isAtStart()) {
                onSaveScroll(position)
            }
        }
    }

    // Wait until items are populated, then perform scroll restoration
    if (!initialScroll.isAtStart()) {
        LaunchedEffect(paginator, initialScroll) {
            snapshotFlow { listState.layoutInfo.totalItemsCount }
                .first { totalItems -> totalItems > initialScroll.index }
            listState.scrollToItem(initialScroll.index, initialScroll.offset)
        }
    }

    DisposableEffect(paginator) {
        onDispose {
            onSaveScroll(
                ScrollPosition(
                    listState.firstVisibleItemIndex,
                    listState.firstVisibleItemScrollOffset
                )
            )
        }
    }

    return listState
}

@Composable
fun <Item : Any> standardPaginatedSlots(
    viewModel: PaginatedViewModel<Item>,
    emptyContent: @Composable () -> Unit
): PaginatedSlots<Item> {
    return PaginatedSlots(
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
        emptyContent = emptyContent,
        errorContent = { state ->
            ErrorContent(
                cause = state.exception,
                onRetry = viewModel::restart
            )
        }
    )
}

@Composable
fun <Item : Any> standardSearchPaginatedSlots(
    viewModel: PaginatedViewModel<Item>
): PaginatedSlots<Item> =
    standardPaginatedSlots(viewModel, ::SearchEmptyContent)

@Composable
fun <Item : Any> standardEmptyablePaginatedSlots(
    viewModel: PaginatedViewModel<Item>,
    emptyMessage: String
): PaginatedSlots<Item> =
    standardPaginatedSlots(viewModel, { EmptyListContent(message = emptyMessage) })