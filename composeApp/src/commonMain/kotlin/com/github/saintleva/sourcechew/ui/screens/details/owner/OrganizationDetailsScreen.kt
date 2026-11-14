package com.github.saintleva.sourcechew.ui.screens.details.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.OrganizationDetails
import com.github.saintleva.sourcechew.ui.common.ErrorContent
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import com.github.saintleva.sourcechew.ui.screens.details.DetailsUiState
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.description_section
import sourcechew.composeapp.generated.resources.members
import sourcechew.composeapp.generated.resources.public_repos

@Composable
fun OrganizationDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: OwnerDetailsViewModel,
    onReposClick: (org: String) -> Unit = {},
    onMembersClick: (org: String) -> Unit = {},
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
                val details = currentState.details as? OrganizationDetails
                if (details != null) {
                    OrganizationDetailsContent(
                        details = details,
                        onReposClick = { onReposClick(details.login) },
                        onMembersClick = { onMembersClick(details.login) },
                    )
                }
            }
        }
    }
}

@Composable
fun OrganizationDetailsContent(
    details: OrganizationDetails,
    modifier: Modifier = Modifier,
    onReposClick: () -> Unit = {},
    onMembersClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        OwnerHeader(details = details, isOrg = true)

        // Description section
        if (!details.description.isNullOrBlank()) {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(Res.string.description_section),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = details.description, style = MaterialTheme.typography.bodyLarge)
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

            // Members Button
            OutlinedButton(
                onClick = onMembersClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Group, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(Res.string.members))
            }
        }

        // Details Base Info Card
        OwnerInfoCard(details = details)

        // Open in Browser
        OpenInBrowserButton(htmlUrl = details.htmlUrl)

        Spacer(modifier = Modifier.height(16.dp))
    }
}
