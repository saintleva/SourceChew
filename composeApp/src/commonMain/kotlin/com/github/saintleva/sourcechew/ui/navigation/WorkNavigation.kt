package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
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
import com.github.saintleva.sourcechew.ui.screens.found.ItemOwnerContent
import com.github.saintleva.sourcechew.ui.screens.found.ItemRepoContent
import com.github.saintleva.sourcechew.ui.screens.found.FoundScreen
import com.github.saintleva.sourcechew.ui.screens.found.FoundViewModel
import com.github.saintleva.sourcechew.ui.screens.search.OwnerSearchScreen
import com.github.saintleva.sourcechew.ui.screens.search.OwnerSearchViewModel
import com.github.saintleva.sourcechew.ui.screens.search.RepoSearchScreen
import com.github.saintleva.sourcechew.ui.screens.search.RepoSearchViewModel
import com.github.saintleva.sourcechew.ui.screens.search.SearchBottomSheet
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.qualifier

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
        Route.Work.Search.Repo
    )
    var showSearchSheet by rememberSaveable { mutableStateOf(false) }

    if (showSearchSheet) {
        SearchBottomSheet(
            onDismissRequest = { showSearchSheet = false },
            onFoundRepo = {
                showSearchSheet = false
                if (backStack.lastOrNull() != Route.Work.Found.Repo) {
                    backStack.add(Route.Work.Found.Repo)
                }
            },
            onFoundOwner = {
                showSearchSheet = false
                if (backStack.lastOrNull() != Route.Work.Found.Owner) {
                    backStack.add(Route.Work.Found.Owner)
                }
            }
        )
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Work.Search.Repo> {
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = { showSearchSheet = true }
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
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = { showSearchSheet = true }
                ) { modifier ->
                    OwnerSearchScreen(
                        modifier = modifier,
                        viewModel = koinViewModel<OwnerSearchViewModel>(),
                        onFound = { backStack.add(Route.Work.Found.Owner) }
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
                        showSearchSheet = true
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
                        itemContent = ::ItemRepoContent
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
                        showSearchSheet = true
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
                        itemContent = ::ItemOwnerContent
                    )
                }
            }
        }
    )
}
