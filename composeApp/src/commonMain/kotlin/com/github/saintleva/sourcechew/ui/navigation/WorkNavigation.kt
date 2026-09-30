package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.ui.screens.found.FoundOwnerContent
import com.github.saintleva.sourcechew.ui.screens.found.FoundRepoContent
import com.github.saintleva.sourcechew.ui.screens.found.FoundScreen
import com.github.saintleva.sourcechew.ui.screens.found.FoundViewModel
import com.github.saintleva.sourcechew.ui.screens.search.OwnerSearchScreen
import com.github.saintleva.sourcechew.ui.screens.search.OwnerSearchViewModel
import com.github.saintleva.sourcechew.ui.screens.search.RepoSearchScreen
import com.github.saintleva.sourcechew.ui.screens.search.RepoSearchViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Reusable container wrapping [WorkScreen] to eliminate boilerplate
 * for drawer item clicks and backstack navigation logic.
 */
@Composable
fun WorkEntryContainer(
    backStack: NavBackStack<NavKey>,
    onMenuItemClick: (Route.Menu) -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    onSearchItemClick: (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    WorkScreen(
        onSearchItemClick = onSearchItemClick ?: {
            if (backStack.lastOrNull() != Route.Work.Search.Repo) {
                backStack.add(Route.Work.Search.Repo)
            }
        },
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
        Route.Work.Search.Repo
    )

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Work.Search.Repo> {
                WorkEntryContainer(
                    backStack = backStack,
                    onMenuItemClick = onMenuItemClick
                ) { modifier ->
                    RepoSearchScreen(
                        modifier = modifier,
                        viewModel = koinViewModel<RepoSearchViewModel>(),
                        onFound = { backStack.add(Route.Work.Found.Repo) }
                    )
                }
            }
            entry<Route.Work.Search.Owner> {
                WorkEntryContainer(
                    backStack = backStack,
                    onMenuItemClick = onMenuItemClick
                ) { modifier ->
                    OwnerSearchScreen(
                        modifier = modifier,
                        viewModel = koinViewModel<OwnerSearchViewModel>(),
                        onFound = { backStack.add(Route.Work.Found.Owner) }
                    )
                }
            }
            entry<Route.Work.Found.Repo> {
                val foundViewModel = koinViewModel<FoundViewModel<RepoSearchConditions, FoundRepo>>()
                WorkEntryContainer(
                    backStack = backStack,
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = {
                        foundViewModel.onNavigationBack()
                        if (backStack.lastOrNull() != Route.Work.Search.Repo) {
                            backStack.add(Route.Work.Search.Repo)
                        }
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
                        itemContent = ::FoundRepoContent
                    )
                }
            }
            entry<Route.Work.Found.Owner> {
                val foundViewModel = koinViewModel<FoundViewModel<OwnerSearchConditions, FoundOwner>>()
                WorkEntryContainer(
                    backStack = backStack,
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = {
                        foundViewModel.onNavigationBack()
                        if (backStack.lastOrNull() != Route.Work.Search.Owner) {
                            backStack.add(Route.Work.Search.Owner)
                        }
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
                        itemContent = ::FoundOwnerContent
                    )
                }
            }
        }
    )
}
