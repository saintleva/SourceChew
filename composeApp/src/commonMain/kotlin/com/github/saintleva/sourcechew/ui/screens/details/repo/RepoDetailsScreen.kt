package com.github.saintleva.sourcechew.ui.screens.details.repo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.ui.common.ExpandableSection
import com.github.saintleva.sourcechew.ui.common.formatDateTime
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import com.github.saintleva.sourcechew.ui.common.titleWithCount
import com.github.saintleva.sourcechew.ui.screens.details.DetailsUiState
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.archived
import sourcechew.composeapp.generated.resources.created
import sourcechew.composeapp.generated.resources.dates
import sourcechew.composeapp.generated.resources.default_branch
import sourcechew.composeapp.generated.resources.fork
import sourcechew.composeapp.generated.resources.forks
import sourcechew.composeapp.generated.resources.license
import sourcechew.composeapp.generated.resources.loading_error
import sourcechew.composeapp.generated.resources.open_in_browser
import sourcechew.composeapp.generated.resources.open_issues
import sourcechew.composeapp.generated.resources.owner
import sourcechew.composeapp.generated.resources.pushed
import sourcechew.composeapp.generated.resources.read_less
import sourcechew.composeapp.generated.resources.read_more
import sourcechew.composeapp.generated.resources.retry_button
import sourcechew.composeapp.generated.resources.stars
import sourcechew.composeapp.generated.resources.topics
import sourcechew.composeapp.generated.resources.updated
import sourcechew.composeapp.generated.resources.watchers
import sourcechew.composeapp.generated.resources.website

@Composable
fun RepoDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: RepoDetailsViewModel,
    onOwnerClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val currentState = state) {
            is DetailsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is DetailsUiState.Error -> {
                ErrorContent(
                    modifier = Modifier.align(Alignment.Center),
                    message = getErrorMessage(currentState.error),
                    onRetry = viewModel::refresh
                )
            }
            is DetailsUiState.InfrastructureError -> {
                ErrorContent(
                    modifier = Modifier.align(Alignment.Center),
                    message = getErrorMessage(currentState.cause),
                    onRetry = viewModel::refresh
                )
            }
            is DetailsUiState.Success -> {
                RepoDetailsContent(
                    details = currentState.details,
                    maxDescriptionLength = settings.maxRepoDescriptionLength,
                    onOwnerClick = onOwnerClick
                )
            }
        }
    }
}

@Composable
private fun RepoDetailsContent(
    details: RepoDetails,
    maxDescriptionLength: Int,
    onOwnerClick: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Repository Name
        Text(
            text = details.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Repository Flags
        if (details.isArchived || details.isFork) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (details.isArchived) {
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(Res.string.archived)) }
                    )
                }
                if (details.isFork) {
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(Res.string.fork)) }
                    )
                }
            }
        }

        // Description
        if (!details.description.isNullOrBlank()) {
            val description = details.description!!
            val isLong = description.length > maxDescriptionLength
            val displayText = if (isLong && !isDescriptionExpanded) {
                description.take(maxDescriptionLength) + "..."
            } else {
                description
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (isLong) {
                    TextButton(
                        onClick = { isDescriptionExpanded = !isDescriptionExpanded },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = stringResource(if (isDescriptionExpanded) Res.string.read_less else Res.string.read_more),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        // Owner Chip / Card
        val isOrg = details.owner.type == OwnerType.ORGANIZATION
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOwnerClick(details.owner.login) }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isOrg) Icons.Default.CorporateFare else Icons.Default.Person,
                        contentDescription = stringResource(Res.string.owner),
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    AsyncImage(
                        model = details.owner.avatarUrl,
                        contentDescription = stringResource(Res.string.owner),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(Res.string.owner),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = details.owner.login,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Star,
                label = stringResource(Res.string.stars),
                value = details.stars.toString()
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ForkRight,
                label = stringResource(Res.string.forks),
                value = details.forks.toString()
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Visibility,
                label = stringResource(Res.string.watchers),
                value = details.subscribersCount.toString()
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.BugReport,
                label = stringResource(Res.string.open_issues),
                value = details.openIssues.toString()
            )
        }

        // Topics (Expandable)
        if (details.topics.isNotEmpty()) {
            ExpandableSection(
                title = titleWithCount(Res.string.topics, details.topics.size),
                initiallyExpanded = false
            ) {
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    details.topics.forEach { topic ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(topic) }
                        )
                    }
                }
            }
        }

        // Info items
        val hasGeneralInfo = details.language != null ||
                details.license != null ||
                details.defaultBranch != null ||
                details.homepageUrl != null

        if (hasGeneralInfo) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    details.language?.let {
                        InfoRow(label = "Language", value = it)
                    }
                    details.license?.let { license ->
                        val licenseName = license.name ?: license.spdxId ?: license.key
                        if (!licenseName.isNullOrBlank()) {
                            InfoRow(label = stringResource(Res.string.license), value = licenseName)
                        }
                    }
                    details.defaultBranch?.let {
                        InfoRow(label = stringResource(Res.string.default_branch), value = it)
                    }
                    details.homepageUrl?.let { homepage ->
                        InfoRow(
                            label = stringResource(Res.string.website),
                            value = homepage,
                            onClick = {
                                val url = if (homepage.startsWith("http://") || homepage.startsWith("https://")) {
                                    homepage
                                } else {
                                    "https://$homepage"
                                }
                                uriHandler.openUri(url)
                            }
                        )
                    }
                }
            }
        }

        // Dates Section (Expandable)
        val hasDates = details.createdAt != null || details.updatedAt != null || details.pushedAt != null
        if (hasDates) {
            ExpandableSection(
                title = stringResource(Res.string.dates),
                initiallyExpanded = false
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        details.createdAt?.let {
                            InfoRow(label = stringResource(Res.string.created), value = it.formatDateTime())
                        }
                        details.updatedAt?.let {
                            InfoRow(label = stringResource(Res.string.updated), value = it.formatDateTime())
                        }
                        details.pushedAt?.let {
                            InfoRow(label = stringResource(Res.string.pushed), value = it.formatDateTime())
                        }
                    }
                }
            }
        }

        // Action: Open in Browser
        Button(
            onClick = { uriHandler.openUri(details.htmlUrl) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.OpenInBrowser, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(Res.string.open_in_browser))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    OutlinedCard(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ) {
            val showLabel = maxWidth >= 75.dp
            val content = @Composable {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (showLabel) {
                        Text(text = label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }

            if (!showLabel) {
                val tooltipState = rememberTooltipState()
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(label)
                        }
                    },
                    state = tooltipState
                ) {
                    content()
                }
            } else {
                content()
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (onClick != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ErrorContent(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.loading_error),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(Res.string.retry_button))
        }
    }
}
