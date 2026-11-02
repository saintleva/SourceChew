package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions

@Composable
fun FoundOwnerScreen(
    modifier: Modifier = Modifier,
    viewModel: FoundViewModel<OwnerSearchConditions, FoundOwner>,
    onOwnerClick: (FoundOwner) -> Unit
) {
    FoundScreen(
        modifier = modifier,
        viewModel = viewModel,
        itemContent = { owner ->
            ItemFoundOwnerContent(
                owner = owner,
                onClick = { onOwnerClick(owner) }
            )
        }
    )
}
