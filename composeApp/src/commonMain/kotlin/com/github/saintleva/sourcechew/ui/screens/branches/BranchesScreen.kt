package com.github.saintleva.sourcechew.ui.screens.branches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.github.saintleva.sourcechew.ui.common.AppendIndicator
import com.github.saintleva.sourcechew.ui.common.EmptyContent
import com.github.saintleva.sourcechew.ui.common.ErrorContent
import com.github.saintleva.sourcechew.ui.common.FullscreenLoading
import com.jamal_aliev.paginator.compose.core.PaginatedSlots
import com.jamal_aliev.paginator.compose.offset.PaginatedLazyColumn
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.branches
import sourcechew.composeapp.generated.resources.commit_sha
import sourcechew.composeapp.generated.resources.default_branch_badge

@Composable
fun BranchesScreen(
    modifier: Modifier = Modifier,
    viewModel: BranchesViewModel
) {
    val paginatorState by viewModel.paginator.collectAsStateWithLifecycle()
    val paginator = paginatorState

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
            BranchCard(
                branchName = defaultBranchName,
                commitSha = defaultBranchRepo?.commitSha,
                isDefault = true
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
            BranchCard(
                branchName = item.name,
                commitSha = item.commitSha,
                isDefault = false
            )
        }
    }
}

@Composable
private fun BranchCard(
    branchName: String,
    commitSha: String?,
    isDefault: Boolean = false
) {
    val cardColors = if (isDefault) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
        )
    } else {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    }

    val border = if (isDefault) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    } else {
        null
    }

    val iconTint = if (isDefault) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant

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
                tint = iconTint
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
                    if (isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.tertiary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                                Text(
                                    text = stringResource(Res.string.default_branch_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    }
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
