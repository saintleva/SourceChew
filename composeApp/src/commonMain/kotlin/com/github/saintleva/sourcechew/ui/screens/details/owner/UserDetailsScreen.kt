package com.github.saintleva.sourcechew.ui.screens.details.owner

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.models.UserDetails
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.bio
import sourcechew.composeapp.generated.resources.organizations

@Composable
fun UserDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: OwnerDetailsViewModel,
    onReposClick: (username: String) -> Unit = {},
    onOrgsClick: (username: String) -> Unit = {},
) {
    OwnerDetailsScreenContainer<UserDetails>(
        viewModel = viewModel,
        modifier = modifier,
    ) { details ->
        UserDetailsContent(
            details = details,
            onReposClick = { onReposClick(details.login) },
            onOrgsClick = { onOrgsClick(details.login) },
        )
    }
}

@Composable
fun UserDetailsContent(
    details: UserDetails,
    modifier: Modifier = Modifier,
    onReposClick: () -> Unit = {},
    onOrgsClick: () -> Unit = {},
) {
    OwnerDetailsContentLayout(
        details = details,
        ownerType = OwnerType.USER,
        modifier = modifier,
        sectionTitle = stringResource(Res.string.bio),
        sectionText = details.bio,
        onReposClick = onReposClick,
        secondaryActionButton = {
            OutlinedButton(
                onClick = onOrgsClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.CorporateFare, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(Res.string.organizations))
            }
        },
    )
}
