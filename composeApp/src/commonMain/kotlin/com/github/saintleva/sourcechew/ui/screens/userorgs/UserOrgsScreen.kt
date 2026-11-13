package com.github.saintleva.sourcechew.ui.screens.userorgs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.ui.common.OwnerListScreen
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.organizations

@Composable
fun UserOrgsScreen(
    modifier: Modifier = Modifier,
    viewModel: UserOrgsViewModel,
    onOrgClick: (Owner) -> Unit = {}
) {
    val paginator by viewModel.paginatorStateFlow.collectAsStateWithLifecycle()

    OwnerListScreen(
        modifier = modifier,
        title = stringResource(Res.string.organizations),
        paginator = paginator,
        viewModel = viewModel,
        onItemClick = onOrgClick
    )
}
