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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.saintleva.sourcechew.ui.screens.PaginatedViewModel
import com.jamal_aliev.paginator.compose.core.PaginatedSlots
import com.jamal_aliev.paginator.compose.offset.PaginatedLazyColumn
import com.jamal_aliev.paginator.core.page.PageState
import com.jamal_aliev.paginator.offset.Paginator

@Composable
fun <Item : Any> PaginatedList(
    viewModel: PaginatedViewModel<Item>,
    key: (Item) -> Any,
    modifier: Modifier = Modifier,
    paginator: Paginator<Item>? = viewModel.paginator,
    slots: PaginatedSlots<Item> = defaultPaginatedSlots(viewModel),
    itemContent: @Composable LazyItemScope.(item: Item, globalIndex: Int, pageIndex: Int, items: List<Item>, page: PageState<Item>) -> Unit
) {
    if (paginator == null) {
        FullscreenLoading(modifier = modifier)
        return
    }

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

    PaginatedLazyColumn(
        paginator = paginator,
        modifier = modifier.fillMaxSize(),
        state = listState,
        key = key,
        slots = slots,
        itemContent = itemContent
    )
}

@Composable
fun <Item : Any> defaultPaginatedSlots(
    viewModel: PaginatedViewModel<Item>
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
        emptyContent = { EmptyContent() },
        errorContent = { state ->
            ErrorContent(
                cause = state.exception,
                onRetry = viewModel::restart
            )
        }
    )
}
