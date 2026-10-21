package com.github.saintleva.sourcechew.ui.screens.details.repo

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import com.github.saintleva.sourcechew.ui.screens.details.DetailsUiState
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.created
import sourcechew.composeapp.generated.resources.default_branch
import sourcechew.composeapp.generated.resources.forks
import sourcechew.composeapp.generated.resources.license
import sourcechew.composeapp.generated.resources.loading_error
import sourcechew.composeapp.generated.resources.open_in_browser
import sourcechew.composeapp.generated.resources.open_issues
import sourcechew.composeapp.generated.resources.owner
import sourcechew.composeapp.generated.resources.pushed
import sourcechew.composeapp.generated.resources.retry_button
import sourcechew.composeapp.generated.resources.stars
import sourcechew.composeapp.generated.resources.updated
import sourcechew.composeapp.generated.resources.watchers

@Composable
fun RepositoryDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: RepositoryDetailsViewModel,
    onOwnerClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                    onOwnerClick = onOwnerClick
                )
            }
        }
    }
}

@Composable
private fun RepoDetailsContent(
    details: RepoDetails,
    onOwnerClick: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Repository Name & Full Name
        Text(
            text = details.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = details.fullName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Description
        if (!details.description.isNullOrBlank()) {
            Text(
                text = details.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Owner Chip / Card
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOwnerClick(details.owner.login) }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = stringResource(Res.string.owner))
                Spacer(modifier = Modifier.width(8.dp))
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
                value = details.watchers.toString()
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.BugReport,
                label = stringResource(Res.string.open_issues),
                value = details.openIssues.toString()
            )
        }

        // Topics
        if (details.topics.isNotEmpty()) {
            FlowRow(
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

        // Info items
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
                details.licenseName?.let {
                    InfoRow(label = stringResource(Res.string.license), value = it)
                }
                details.defaultBranch?.let {
                    InfoRow(label = stringResource(Res.string.default_branch), value = it)
                }
                details.createdAt?.let {
                    InfoRow(label = stringResource(Res.string.created), value = it.toString())
                }
                details.updatedAt?.let {
                    InfoRow(label = stringResource(Res.string.updated), value = it.toString())
                }
                details.pushedAt?.let {
                    InfoRow(label = stringResource(Res.string.pushed), value = it.toString())
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

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
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
