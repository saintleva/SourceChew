package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.ui.screens.details.owner.OwnerDetailsScreen
import com.github.saintleva.sourcechew.ui.screens.details.owner.OwnerDetailsViewModel
import com.github.saintleva.sourcechew.ui.screens.details.repo.RepoDetailsScreen
import com.github.saintleva.sourcechew.ui.screens.details.repo.RepoDetailsViewModel
import com.github.saintleva.sourcechew.ui.screens.found.FoundScreen
import com.github.saintleva.sourcechew.ui.screens.found.FoundViewModel
import com.github.saintleva.sourcechew.ui.screens.found.ItemOwnerContent
import com.github.saintleva.sourcechew.ui.screens.found.ItemRepoContent
import com.github.saintleva.sourcechew.ui.screens.search.SearchTabScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.qualifier
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.refresh_button

/**
 * Reusable container wrapping [WorkScreen] to eliminate boilerplate
 * for drawer item clicks and backstack navigation logic.
 */
@Composable
fun WorkEntryContainer(
    onMenuItemClick: (Route.Menu) -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    onSearchItemClick: () -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    WorkScreen(
        onSearchItemClick = onSearchItemClick,
        onMenuItemClick = onMenuItemClick,
        actions = actions,
        content = content
    )
}

@Composable
fun WorkNavigation(
    onMenuItemClick: (Route.Menu) -> Unit,
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = workSerializersModule
        },
        Route.Work.Search()
    )

    fun navigateToSearch() {
        if (backStack.lastOrNull() !is Route.Work.Search) {
            backStack.add(Route.Work.Search())
        }
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Work.Search> { key ->
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = ::navigateToSearch
                ) { modifier ->
                    SearchTabScreen(
                        modifier = modifier,
                        initialTab = key.tab,
                        onFoundRepo = {
                            if (backStack.lastOrNull() != Route.Work.Found.Repo) {
                                backStack.add(Route.Work.Found.Repo)
                            }
                        },
                        onFoundOwner = {
                            if (backStack.lastOrNull() != Route.Work.Found.Owner) {
                                backStack.add(Route.Work.Found.Owner)
                            }
                        }
                    )
                }
            }
            entry<Route.Work.Found.Repo> {
                val foundViewModel = koinViewModel<FoundViewModel<RepoSearchConditions, FoundRepo>>(
                    qualifier<FoundRepo>()
                )
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = {
                        foundViewModel.onNavigationBack()
                        navigateToSearch()
                    },
                    actions = {
                        BackIcon {
                            foundViewModel.onNavigationBack()
                            backStack.pop()
                        }
                    }
                ) { modifier ->
                    FoundScreen(
                        modifier = modifier,
                        viewModel = foundViewModel,
                        itemContent = { repo ->
                            ItemRepoContent(repo) {
                                backStack.add(Route.Work.RepositoryDetails(repo.owner.login, repo.name))
                            }
                        }
                    )
                }
            }
            entry<Route.Work.Found.Owner> {
                val foundViewModel = koinViewModel<FoundViewModel<OwnerSearchConditions, FoundOwner>>(
                    qualifier<FoundOwner>()
                )
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = {
                        foundViewModel.onNavigationBack()
                        navigateToSearch()
                    },
                    actions = {
                        BackIcon {
                            foundViewModel.onNavigationBack()
                            backStack.pop()
                        }
                    }
                ) { modifier ->
                    FoundScreen(
                        modifier = modifier,
                        viewModel = foundViewModel,
                        itemContent = { owner ->
                            ItemOwnerContent(owner) {
                                backStack.add(Route.Work.OwnerDetails(owner.login))
                            }
                        }
                    )
                }
            }
            entry<Route.Work.RepositoryDetails> { key ->
                val detailsViewModel = koinViewModel<RepoDetailsViewModel>(
                    parameters = { parametersOf(key.owner, key.name) }
                )
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = ::navigateToSearch,
                    actions = {
                        IconButton(onClick = detailsViewModel::refresh) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(Res.string.refresh_button))
                        }
                        BackIcon {
                            backStack.pop()
                        }
                    }
                ) { modifier ->
                    RepoDetailsScreen(
                        modifier = modifier,
                        viewModel = detailsViewModel,
                        onOwnerClick = { ownerLogin ->
                            backStack.add(Route.Work.OwnerDetails(ownerLogin))
                        }
                    )
                }
            }
            entry<Route.Work.OwnerDetails> { key ->
                val detailsViewModel = koinViewModel<OwnerDetailsViewModel>(
                    parameters = { parametersOf(key.login) }
                )
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = ::navigateToSearch,
                    actions = {
                        IconButton(onClick = detailsViewModel::refresh) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(Res.string.refresh_button))
                        }
                        BackIcon {
                            backStack.pop()
                        }
                    }
                ) { modifier ->
                    OwnerDetailsScreen(
                        modifier = modifier,
                        viewModel = detailsViewModel
                    )
                }
            }
        }
    )
}
