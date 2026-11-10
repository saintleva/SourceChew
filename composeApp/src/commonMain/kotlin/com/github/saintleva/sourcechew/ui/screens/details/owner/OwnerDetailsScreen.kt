package com.github.saintleva.sourcechew.ui.screens.details.owner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.OrganizationDetails
import com.github.saintleva.sourcechew.domain.models.UserDetails
import com.github.saintleva.sourcechew.ui.common.ErrorContent
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import com.github.saintleva.sourcechew.ui.screens.details.DetailsUiState

@Composable
fun OwnerDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: OwnerDetailsViewModel,
    onUserReposClick: (String) -> Unit = {},
    onUserOrgsClick: (String) -> Unit = {},
    onOrgReposClick: (String) -> Unit = {},
    onOrgMembersClick: (String) -> Unit = {},
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
                when (val details = currentState.details) {
                    is UserDetails -> {
                        UserDetailsScreen(
                            details = details,
                            onReposClick = { onUserReposClick(details.login) },
                            onOrgsClick = { onUserOrgsClick(details.login) },
                        )
                    }
                    is OrganizationDetails -> {
                        OrganizationDetailsScreen(
                            details = details,
                            onReposClick = { onOrgReposClick(details.login) },
                            onMembersClick = { onOrgMembersClick(details.login) },
                        )
                    }
                }
            }
        }
    }
}
