package com.github.saintleva.sourcechew.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.end_of_list
import sourcechew.composeapp.generated.resources.loading_more_error
import sourcechew.composeapp.generated.resources.retry_button


/**
 * Adds a footer to LazyColumn to display the state of the Paging APPEND operation.
 * Includes a loading indicator, an error message with a retry button, or an end-of-list message.
 *
 * @param T The type of items in LazyPagingItems.
 * @param lazyPagingItems The pagination items.
 */
fun <T : Any> LazyListScope.pagingAppendFooter(
    lazyPagingItems: LazyPagingItems<T>
) {
    // Do not display the footer if refresh is still in progress or in an error state,
    // or if the list is empty and refresh has already completed (this is handled by emptyContent).
    // This prevents showing "end of list" or an append error immediately after an empty refresh result.
    if (lazyPagingItems.loadState.refresh is LoadState.Loading ||
        lazyPagingItems.loadState.refresh is LoadState.Error) {
        return
    }
    // Also, if the list is empty and refresh is complete, do not show append indicators,
    // as emptyContent should have already handled this.
    // This condition is needed to avoid showing "end of list" right after "nothing found".
    if (lazyPagingItems.itemCount == 0 && lazyPagingItems.loadState.refresh is LoadState.NotLoading) {
        return
    }

    when (val appendState = lazyPagingItems.loadState.append) {
        is LoadState.Loading -> {
            item(key = "paging_append_loading_indicator") {
                PagingAppendLoadingIndicatorItem()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_append_error_indicator") {
                PagingAppendErrorItem(
                    error = appendState.error,
                    onRetry = { lazyPagingItems.retry() }
                )
            }
        }
        is LoadState.NotLoading -> {
            if (appendState.endOfPaginationReached && lazyPagingItems.itemCount > 0) {
                // Show "end of list" only if there are items.
                // If itemCount == 0, this state should have already been handled by emptyContent.
                item(key = "paging_append_end_of_list_indicator") {
                    PagingAppendEndOfListIndicator()
                }
            }
        }
    }
}

// Helper Composables for indicators/errors/end-of-list within LazyColumn

@Composable
fun PagingAppendLoadingIndicatorItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun PagingAppendErrorItem(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Napier.d(tag = "pagingAppendFooter") { error.message ?: "Unknown error" }
            Text(
                text = stringResource(Res.string.loading_more_error),
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getErrorMessage(error),
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text(stringResource(Res.string.retry_button))
            }
        }
    }
}

@Composable
fun PagingAppendEndOfListIndicator(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.end_of_list),
        modifier = modifier.fillMaxWidth().padding(16.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
    )
}