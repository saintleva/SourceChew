package com.github.saintleva.sourcechew.ui.screens.details.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import com.github.saintleva.sourcechew.ui.common.StatCard
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.ui.common.ErrorContent
import com.github.saintleva.sourcechew.ui.common.OpenInBrowserButton
import com.github.saintleva.sourcechew.ui.common.formatDateTime
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import com.github.saintleva.sourcechew.ui.screens.details.DetailsUiState
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.company
import sourcechew.composeapp.generated.resources.created
import sourcechew.composeapp.generated.resources.followers
import sourcechew.composeapp.generated.resources.location
import sourcechew.composeapp.generated.resources.open_in_browser
import sourcechew.composeapp.generated.resources.organization
import sourcechew.composeapp.generated.resources.public_gists
import sourcechew.composeapp.generated.resources.public_repos
import sourcechew.composeapp.generated.resources.user
import sourcechew.composeapp.generated.resources.website

@Composable
inline fun <reified Details : OwnerDetailsBase> OwnerDetailsScreenContainer(
    viewModel: OwnerDetailsViewModel,
    modifier: Modifier = Modifier,
    crossinline content: @Composable (details: Details) -> Unit,
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
                (currentState.details as? Details)?.let { details ->
                    content(details)
                }
            }
        }
    }
}

@Composable
fun OwnerDetailsContentLayout(
    details: OwnerDetailsBase,
    ownerType: OwnerType,
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
    sectionText: String? = null,
    onReposClick: () -> Unit,
    secondaryActionButton: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OwnerHeader(details = details, ownerType = ownerType)

        if (!sectionTitle.isNullOrBlank() && !sectionText.isNullOrBlank()) {
            TextSectionCard(title = sectionTitle, text = sectionText)
        }

        OwnerStatsRow(details = details)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RepositoriesButton(
                publicReposCount = details.publicRepos,
                onClick = onReposClick,
            )
            secondaryActionButton()
        }

        OwnerInfoCard(details = details)

        OpenInBrowserButton(htmlUrl = details.htmlUrl)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TextSectionCard(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun RepositoriesButton(
    publicReposCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(Icons.Default.Source, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("${stringResource(Res.string.public_repos)} ($publicReposCount)")
    }
}

@Composable
fun OwnerHeader(
    details: OwnerDetailsBase,
    ownerType: OwnerType,
    modifier: Modifier = Modifier,
    extraChip: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
                imageVector = when (ownerType) {
                    OwnerType.USER -> Icons.Default.Person
                    OwnerType.ORGANIZATION -> Icons.Default.CorporateFare
                },
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            AsyncImage(
                model = details.avatarUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Column {
            if (!details.name.isNullOrBlank()) {
                Text(
                    text = details.name!!,
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Non-clickable surface badge for owner type
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = stringResource(
                            when (ownerType) {
                                OwnerType.USER -> Res.string.user
                                OwnerType.ORGANIZATION -> Res.string.organization
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                extraChip?.invoke()
            }
        }
    }
}

@Composable
fun OwnerStatsRow(
    details: OwnerDetailsBase,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Group,
            label = stringResource(Res.string.followers),
            value = details.followers.toString(),
        )
    }
}

@Composable
fun OwnerInfoCard(
    details: OwnerDetailsBase,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    val hasInfo = details.company != null ||
            details.location != null ||
            details.blog != null ||
            details.email != null ||
            details.createdAt != null

    if (hasInfo) {
        Card(
            modifier = modifier.fillMaxWidth(),
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
                        value = it.formatDateTime(),
                    )
                }
            }
        }
    }
}



@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
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
