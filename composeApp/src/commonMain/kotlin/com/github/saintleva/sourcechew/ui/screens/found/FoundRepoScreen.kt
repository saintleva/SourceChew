package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions

@Composable
fun FoundRepoScreen(
    modifier: Modifier = Modifier,
    viewModel: FoundViewModel<RepoSearchConditions, FoundRepo>,
    onRepoClick: (RepoRef) -> Unit,
    onOwnerClick: (String) -> Unit
) {
    val maxDescriptionLength by viewModel.maxDescriptionLength.collectAsStateWithLifecycle(
        AppSettings.default.maxFoundRepoDescriptionLength
    )

    FoundScreen(
        modifier = modifier,
        viewModel = viewModel,
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
