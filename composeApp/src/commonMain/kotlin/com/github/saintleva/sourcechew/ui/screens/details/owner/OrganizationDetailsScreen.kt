package com.github.saintleva.sourcechew.ui.screens.details.owner

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.OrganizationDetails
import com.github.saintleva.sourcechew.domain.models.OwnerType
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.description_section
import sourcechew.composeapp.generated.resources.members

@Composable
fun OrganizationDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: OwnerDetailsViewModel,
    onReposClick: (org: String) -> Unit = {},
    onMembersClick: (org: String) -> Unit = {},
) {
    OwnerDetailsScreenContainer<OrganizationDetails>(
        viewModel = viewModel,
        modifier = modifier,
    ) { details ->
        OrganizationDetailsContent(
            details = details,
            onReposClick = { onReposClick(details.login) },
            onMembersClick = { onMembersClick(details.login) },
        )
    }
}

@Composable
fun OrganizationDetailsContent(
    details: OrganizationDetails,
    modifier: Modifier = Modifier,
    onReposClick: () -> Unit = {},
    onMembersClick: () -> Unit = {},
) {
    OwnerDetailsContentLayout(
        details = details,
        ownerType = OwnerType.ORGANIZATION,
        modifier = modifier,
        sectionTitle = stringResource(Res.string.description_section),
        sectionText = details.description,
        onReposClick = onReposClick,
        secondaryActionButton = {
            OutlinedButton(
                onClick = onMembersClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Group, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(Res.string.members))
            }
        },
    )
}
