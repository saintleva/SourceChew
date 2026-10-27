package com.github.saintleva.sourcechew.ui.screens.branches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.github.saintleva.sourcechew.domain.models.RepoBranch
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import com.jamal_aliev.paginator.compose.core.PaginatedSlots
import com.jamal_aliev.paginator.compose.offset.PaginatedLazyColumn
import com.jamal_aliev.paginator.core.page.PageState
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.branches
import sourcechew.composeapp.generated.resources.commit_sha
import sourcechew.composeapp.generated.resources.default_branch_badge
import sourcechew.composeapp.generated.resources.loading_error
import sourcechew.composeapp.generated.resources.loading_more_error
import sourcechew.composeapp.generated.resources.no_items_found_description
import sourcechew.composeapp.generated.resources.no_items_found_title
import sourcechew.composeapp.generated.resources.retry_button

@Composable
fun BranchesScreen(
    modifier: Modifier = Modifier,
    viewModel: BranchesViewModel
) {
    val paginatorState by viewModel.paginator.collectAsStateWithLifecycle()
    val paginator = paginatorState

    if (paginator == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
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

    val defaultBranchName = viewModel.defaultBranch
    val defaultBranchRepo = remember(paginatorState) {
        if (defaultBranchName.isNullOrBlank()) null
        else {
            val cache = paginator.cache
            var found: RepoBranch? = null
            for (page in cache.pages) {
                val pageState = cache.getStateOf(page)
                val data = pageState?.data ?: continue
                found = data.firstOrNull { it.name == defaultBranchName }
                if (found != null) break
            }
            found
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(Res.string.branches),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (!defaultBranchName.isNullOrBlank()) {
            PinnedDefaultBranchCard(
                branchName = defaultBranchName,
                commitSha = defaultBranchRepo?.commitSha
            )
        }

        PaginatedLazyColumn<RepoBranch>(
            paginator = paginator,
            modifier = Modifier.fillMaxSize(),
            state = listState,
            key = { it.name },
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
        ) { item, _, _, _, _ ->
            if (item.name == viewModel.defaultBranch) return@PaginatedLazyColumn
            val isDefault = item.name == viewModel.defaultBranch
            BranchItemCard(
                branch = item,
                isDefault = isDefault
            )
        }
    }
}

@Composable
private fun PinnedDefaultBranchCard(
    branchName: String,
    commitSha: String?
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
    )
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = cardColors,
        border = border
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ForkRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = branchName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = stringResource(Res.string.default_branch_badge),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            labelColor = MaterialTheme.colorScheme.onTertiary
                        )
                    )
                }
                if (!commitSha.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.commit_sha, commitSha.take(7)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BranchItemCard(
    branch: RepoBranch,
    isDefault: Boolean
) {
    val cardColors = if (isDefault) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        )
    } else {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    }

    val border = if (isDefault) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    } else {
        null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = cardColors,
        border = border
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ForkRight,
                contentDescription = null,
                tint = if (isDefault) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = branch.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = stringResource(Res.string.default_branch_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.commit_sha, branch.commitSha.take(7)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AppendIndicator(
    errorState: PageState.ErrorState<RepoBranch>,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.loading_more_error),
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = getErrorMessage(errorState.exception),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = onRetry) {
            Text(stringResource(Res.string.retry_button))
        }
    }
}

@Composable
private fun FullscreenLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.no_items_found_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(Res.string.no_items_found_description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ErrorContent(cause: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.loading_error),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = getErrorMessage(cause),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(Res.string.retry_button))
        }
    }
}
