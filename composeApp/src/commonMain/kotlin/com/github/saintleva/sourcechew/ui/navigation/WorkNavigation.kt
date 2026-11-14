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
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.ui.screens.branches.BranchesScreen
import com.github.saintleva.sourcechew.ui.screens.branches.BranchesViewModel
import com.github.saintleva.sourcechew.ui.screens.details.owner.OrganizationDetailsScreen
import com.github.saintleva.sourcechew.ui.screens.details.owner.OwnerDetailsViewModel
import com.github.saintleva.sourcechew.ui.screens.details.owner.UserDetailsScreen
import com.github.saintleva.sourcechew.ui.screens.ownerrepos.OwnerReposViewModel
import com.github.saintleva.sourcechew.ui.screens.details.repo.RepoDetailsScreen
import com.github.saintleva.sourcechew.ui.screens.details.repo.RepoDetailsViewModel
import com.github.saintleva.sourcechew.ui.screens.found.FoundOwnerScreen
import com.github.saintleva.sourcechew.ui.screens.found.FoundRepoScreen
import com.github.saintleva.sourcechew.ui.screens.found.FoundViewModel
import com.github.saintleva.sourcechew.ui.screens.members.MembersScreen
import com.github.saintleva.sourcechew.ui.screens.members.MembersViewModel
import com.github.saintleva.sourcechew.ui.screens.userorgs.UserOrgsScreen
import com.github.saintleva.sourcechew.ui.screens.userorgs.UserOrgsViewModel
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
                val maxDescriptionLength by foundViewModel.maxDescriptionLength.collectAsStateWithLifecycle(
                    AppSettings.default.maxFoundRepoDescriptionLength
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
                    FoundRepoScreen(
                        modifier = modifier,
                        viewModel = foundViewModel,
                        maxDescriptionLength = maxDescriptionLength,
                        onRepoClick = { repoRef ->
                            backStack.add(Route.Work.Details.Repo(repoRef))
                        },
                        onOwnerClick = { owner ->
                            when (owner.type) {
                                OwnerType.ORGANIZATION -> backStack.add(Route.Work.Details.Organization(owner.login))
                                OwnerType.USER -> backStack.add(Route.Work.Details.User(owner.login))
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
                    FoundOwnerScreen(
                        modifier = modifier,
                        viewModel = foundViewModel,
                        onOwnerClick = { owner ->
                            when (owner.type) {
                                OwnerType.ORGANIZATION -> backStack.add(Route.Work.Details.Organization(owner.login))
                                OwnerType.USER -> backStack.add(Route.Work.Details.User(owner.login))
                            }
                        }
                    )
                }
            }
            entry<Route.Work.Details.Repo> { key ->
                val detailsViewModel = koinViewModel<RepoDetailsViewModel>(
                    parameters = { parametersOf(key.repoRef) }
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
                        onOwnerClick = { owner ->
                            when (owner.type) {
                                OwnerType.ORGANIZATION -> backStack.add(Route.Work.Details.Organization(owner.login))
                                OwnerType.USER -> backStack.add(Route.Work.Details.User(owner.login))
                            }
                        },
                        onBranchesClick = { owner, repo ->
                            backStack.add(Route.Work.Branches(RepoRef(owner, repo)))
                        }
                    )
                }
            }
            entry<Route.Work.Details.User> { key ->
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
                    UserDetailsScreen(
                        modifier = modifier,
                        viewModel = detailsViewModel,
                        onReposClick = { username ->
                            backStack.add(Route.Work.OwnerRepos(username))
                        },
                        onOrgsClick = { username ->
                            backStack.add(Route.Work.UserOrgs(username))
                        }
                    )
                }
            }
            entry<Route.Work.Details.Organization> { key ->
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
                    OrganizationDetailsScreen(
                        modifier = modifier,
                        viewModel = detailsViewModel,
                        onReposClick = { org ->
                            backStack.add(Route.Work.OwnerRepos(org))
                        },
                        onMembersClick = { org ->
                            backStack.add(Route.Work.Members(org))
                        }
                    )
                }
            }
            entry<Route.Work.OwnerRepos> { key ->
                val ownerReposViewModel = koinViewModel<OwnerReposViewModel>(
                    parameters = { parametersOf(key.owner) }
                )
                val maxDescriptionLength by ownerReposViewModel.maxDescriptionLength.collectAsStateWithLifecycle(
                    AppSettings.default.maxFoundRepoDescriptionLength
                )
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = ::navigateToSearch,
                    actions = {
                        BackIcon {
                            backStack.pop()
                        }
                    }
                ) { modifier ->
                    FoundRepoScreen(
                        modifier = modifier,
                        viewModel = ownerReposViewModel,
                        maxDescriptionLength = maxDescriptionLength,
                        showMetadata = false,
                        onRepoClick = { repoRef ->
                            backStack.add(Route.Work.Details.Repo(repoRef))
                        },
                        onOwnerClick = { owner ->
                            when (owner.type) {
                                OwnerType.ORGANIZATION -> backStack.add(Route.Work.Details.Organization(owner.login))
                                OwnerType.USER -> backStack.add(Route.Work.Details.User(owner.login))
                            }
                        }
                    )
                }
            }
            entry<Route.Work.Branches> { key ->
                val branchesViewModel = koinViewModel<BranchesViewModel>(
                    parameters = { parametersOf(key.repoRef) }
                )
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = ::navigateToSearch,
                    actions = {
                        IconButton(onClick = branchesViewModel::refresh) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(Res.string.refresh_button))
                        }
                        BackIcon {
                            backStack.pop()
                        }
                    }
                ) { modifier ->
                    BranchesScreen(
                        modifier = modifier,
                        viewModel = branchesViewModel
                    )
                }
            }
            entry<Route.Work.Members> { key ->
                val membersViewModel = koinViewModel<MembersViewModel>(
                    parameters = { parametersOf(key.org) }
                )
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = ::navigateToSearch,
                    actions = {
                        BackIcon {
                            backStack.pop()
                        }
                    }
                ) { modifier ->
                    MembersScreen(
                        modifier = modifier,
                        viewModel = membersViewModel,
                        onMemberClick = { member ->
                            backStack.add(Route.Work.Details.User(member.login))
                        }
                    )
                }
            }
            entry<Route.Work.UserOrgs> { key ->
                val userOrgsViewModel = koinViewModel<UserOrgsViewModel>(
                    parameters = { parametersOf(key.username) }
                )
                WorkEntryContainer(
                    onMenuItemClick = onMenuItemClick,
                    onSearchItemClick = ::navigateToSearch,
                    actions = {
                        BackIcon {
                            backStack.pop()
                        }
                    }
                ) { modifier ->
                    UserOrgsScreen(
                        modifier = modifier,
                        viewModel = userOrgsViewModel,
                        onOrgClick = { org ->
                            backStack.add(Route.Work.Details.Organization(org.login))
                        }
                    )
                }
            }
        }
    )
}
