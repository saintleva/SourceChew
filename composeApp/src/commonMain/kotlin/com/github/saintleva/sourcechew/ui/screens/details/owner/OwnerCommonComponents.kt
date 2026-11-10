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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import com.github.saintleva.sourcechew.ui.common.formatDateTime
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
fun OwnerHeader(
    details: OwnerDetailsBase,
    isOrg: Boolean,
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
                imageVector = if (isOrg) Icons.Default.CorporateFare else Icons.Default.Person,
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
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            stringResource(
                                if (isOrg) Res.string.organization else Res.string.user
                            )
                        )
                    },
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
) {
    OutlinedCard(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ) {
            val showLabel = maxWidth >= 75.dp
            val content = @Composable {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                    state = tooltipState,
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

@Composable
fun OpenInBrowserButton(
    htmlUrl: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Button(
        onClick = { uriHandler.openUri(htmlUrl) },
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(Icons.Default.OpenInBrowser, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(Res.string.open_in_browser))
    }
}
