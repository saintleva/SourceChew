package com.github.saintleva.sourcechew.ui.screens.details.owner

import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import com.github.saintleva.sourcechew.ui.screens.details.DetailsUiState
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.bio
import sourcechew.composeapp.generated.resources.company
import sourcechew.composeapp.generated.resources.created
import sourcechew.composeapp.generated.resources.followers
import sourcechew.composeapp.generated.resources.following
import sourcechew.composeapp.generated.resources.loading_error
import sourcechew.composeapp.generated.resources.location
import sourcechew.composeapp.generated.resources.open_in_browser
import sourcechew.composeapp.generated.resources.organization
import sourcechew.composeapp.generated.resources.public_gists
import sourcechew.composeapp.generated.resources.public_repos
import sourcechew.composeapp.generated.resources.retry_button
import sourcechew.composeapp.generated.resources.user
import sourcechew.composeapp.generated.resources.website

@Composable
fun OwnerDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: OwnerDetailsViewModel,
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
                    onRetry = viewModel::refresh,
                )
            }
            is DetailsUiState.InfrastructureError -> {
                ErrorContent(
                    modifier = Modifier.align(Alignment.Center),
                    message = getErrorMessage(currentState.cause),
                    onRetry = viewModel::refresh,
                )
            }
            is DetailsUiState.Success -> {
                OwnerDetailsContent(details = currentState.details)
            }
        }
    }
}

@Composable
private fun OwnerDetailsContent(details: OwnerDetailsBase) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header with Avatar and Names
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (details.type == OwnerType.ORGANIZATION) {
                        Icons.Default.CorporateFare
                    } else {
                        Icons.Default.Person
                    },
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Column {
                if (!details.name.isNullOrBlank()) {
                    Text(
                        text = details.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = "@${details.login}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            stringResource(
                                if (details.type == OwnerType.ORGANIZATION) Res.string.organization else Res.string.user
                            )
                        )
                    },
                )
            }
        }

        // Bio (for user) or Description (for org)
        if (!details.bio.isNullOrBlank()) {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(Res.string.bio),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = details.bio, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Source,
                label = stringResource(Res.string.public_repos),
                value = details.publicRepos.toString(),
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Code,
                label = stringResource(Res.string.public_gists),
                value = details.publicGists.toString(),
            )
            if (details.type == OwnerType.USER) {
                details.followers?.let {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Group,
                        label = stringResource(Res.string.followers),
                        value = it.toString(),
                    )
                }
                details.following?.let {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.PersonAdd,
                        label = stringResource(Res.string.following),
                        value = it.toString(),
                    )
                }
            }
        }

        // DetailsBase card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                details.company?.let {
                    DetailRow(
                        icon = Icons.Default.Business,
                        label = stringResource(Res.string.company),
                        value = it,
                    )
                }
                details.location?.let {
                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = stringResource(Res.string.location),
                        value = it,
                    )
                }
                details.blog?.let {
                    DetailRow(
                        icon = Icons.Default.Link,
                        label = stringResource(Res.string.website),
                        value = it,
                        onClick = {
                            val url = if (it.startsWith("http://") || it.startsWith("https://")) it else "https://$it"
                            uriHandler.openUri(url)
                        },
                    )
                }
                details.email?.let {
                    DetailRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = it,
                    )
                }
                details.createdAt?.let {
                    DetailRow(
                        icon = Icons.Default.Person,
                        label = stringResource(Res.string.created),
                        value = it.toString(),
                    )
                }
            }
        }

        // Action: Open in Browser
        Button(
            onClick = { uriHandler.openUri(details.htmlUrl) },
            modifier = Modifier.fillMaxWidth(),
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
    value: String,
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (onClick != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ErrorContent(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.loading_error),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(Res.string.retry_button))
        }
    }
}
