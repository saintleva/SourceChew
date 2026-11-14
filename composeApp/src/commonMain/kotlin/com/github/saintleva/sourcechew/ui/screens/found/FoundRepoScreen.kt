package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.ui.screens.PaginatedViewModel
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.found_repositories

@Composable
fun FoundRepoScreen(
    modifier: Modifier = Modifier,
    viewModel: PaginatedViewModel<FoundRepo>,
    maxDescriptionLength: Int = AppSettings.default.maxFoundRepoDescriptionLength,
    showMetadata: Boolean = true,
    onRepoClick: (RepoRef) -> Unit,
    onOwnerClick: (String) -> Unit
) {
    FoundScreen(
        modifier = modifier,
        viewModel = viewModel,
        itemsTitle = if (showMetadata) stringResource(Res.string.found_repositories) else null,
        showMetadata = showMetadata,
        itemContent = { repo ->
            ItemFoundRepoContent(
                repo = repo,
                maxDescriptionLength = maxDescriptionLength,
                onRepoClick = { onRepoClick(RepoRef(repo.owner.login, repo.name)) },
                onOwnerClick = { onOwnerClick(repo.owner.login) }
            )
        }
    )
}
