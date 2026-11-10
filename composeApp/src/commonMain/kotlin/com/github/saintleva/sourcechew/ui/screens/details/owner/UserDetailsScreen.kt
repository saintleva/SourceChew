package com.github.saintleva.sourcechew.ui.screens.details.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.UserDetails
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.bio
import sourcechew.composeapp.generated.resources.organizations
import sourcechew.composeapp.generated.resources.public_repos

@Composable
fun UserDetailsScreen(
    details: UserDetails,
    modifier: Modifier = Modifier,
    onReposClick: () -> Unit = {},
    onOrgsClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        OwnerHeader(details = details, isOrg = false)

        // Bio section
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
        OwnerStatsRow(details = details)

        // Navigation Action Buttons (Design matching RepoDetailsScreen)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Repositories Button
            Button(
                onClick = onReposClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Source, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("${stringResource(Res.string.public_repos)} (${details.publicRepos})")
            }

            // User Organizations Button
            OutlinedButton(
                onClick = onOrgsClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.CorporateFare, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(Res.string.organizations))
            }
        }

        // Details Base Info Card
        OwnerInfoCard(details = details)

        // Open in Browser
        OpenInBrowserButton(htmlUrl = details.htmlUrl)

        Spacer(modifier = Modifier.height(16.dp))
    }
}
