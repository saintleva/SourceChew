package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.ui.screens.PaginatedViewModel
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.found_owners

@Composable
fun FoundOwnerScreen(
    modifier: Modifier = Modifier,
    viewModel: PaginatedViewModel<FoundOwner>,
    onOwnerClick: (FoundOwner) -> Unit
) {
    FoundScreen(
        modifier = modifier,
        viewModel = viewModel,
        itemsTitle = stringResource(Res.string.found_owners),
        itemContent = { owner ->
            ItemFoundOwnerContent(
                owner = owner,
                onClick = { onOwnerClick(owner) }
            )
        }
    )
}
