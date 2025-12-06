package com.github.saintleva.sourcechew.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import sourcechew.composeapp.generated.resources.loading_error
import sourcechew.composeapp.generated.resources.no_items_found_description
import sourcechew.composeapp.generated.resources.no_items_found_title
import sourcechew.composeapp.generated.resources.refresh_button
import sourcechew.composeapp.generated.resources.retry_button


/**
 * Composable function for centralized handling of Paging 3 full-screen load states (refresh).
 * Displays full-screen states for `refresh` (loading, error, empty list)
 * and passes control to `content` for displaying the list when data is available.
 * `append`/`prepend` states should be handled within the `content` using extensions for LazyListScope.
 *
 * @param T The type of items in LazyPagingItems.
 * @param lazyPagingItems The pagination items.
 * @param modifier Modifier for the root Box.
 * @param onRetryRefresh Function to call on initial load error. Defaults to `lazyPagingItems.refresh()`.
 * @param loadingContent Composable for displaying the full-screen loading indicator.
 * @param errorContent Composable for displaying the full-screen error message.
 * @param emptyContent Composable for displaying a message when the list is empty after loading.
 * @param content The main content to display when data is available (usually a LazyColumn).
 */
@Composable
fun <T : Any> HandlePagingLoadStates(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    onRetryRefresh: () -> Unit = { lazyPagingItems.refresh() },
    loadingContent: @Composable BoxScope.() -> Unit = {
        DefaultPagingLoadingIndicator(modifier = Modifier.align(Alignment.Center))
    },
    errorContent: @Composable BoxScope.(error: Throwable, retryAction: () -> Unit) -> Unit = { error, retry ->
        Napier.d(tag = "HandlePagingLoadStates") { error.message ?: "Unknown error" }
        DefaultPagingErrorContent(
            error = error,
            onRetry = retry,
            modifier = Modifier.align(Alignment.Center).padding(16.dp)
        )
    },
    emptyContent: @Composable BoxScope.(retryAction: () -> Unit) -> Unit = { retry ->
        DefaultPagingEmptyContent(
            onRetry = retry,
            modifier = Modifier.align(Alignment.Center).padding(16.dp)
        )
    },
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (val refreshState = lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                loadingContent()
            }
            is LoadState.Error -> {
                errorContent(refreshState.error, onRetryRefresh)
            }
            is LoadState.NotLoading -> {
                if (lazyPagingItems.itemCount == 0) {
                    // Simplified check for "empty state" when itemCount == 0.
                    // If refresh is complete and there are no items, consider it an "empty" state.
                    emptyContent(onRetryRefresh)
                } else {
                    // Data is available, display the main content (LazyColumn)
                    content()
                }
            }
        }
    }
}

// Default implementations for full-screen states (can be customized or overridden)

@Composable
fun DefaultPagingLoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun DefaultPagingEmptyContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(), // To occupy all available space
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(Res.string.no_items_found_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(Res.string.no_items_found_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(Res.string.refresh_button))
        }
    }
}

@Composable
fun DefaultPagingErrorContent(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, // This modifier is passed from the parent BoxScope.
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.loading_error), // A generic title like "An error occurred".
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = getErrorMessage(error), // The specific, localized error message.
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(Res.string.retry_button))
        }
    }
}
