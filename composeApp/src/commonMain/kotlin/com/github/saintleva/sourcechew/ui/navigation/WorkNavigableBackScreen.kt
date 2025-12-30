package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.saintleva.sourcechew.ui.screens.found.FoundScreen
import com.github.saintleva.sourcechew.ui.screens.search.SearchScreen
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun WorkNavigation(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = workSerializersModule
        },
        Route.Work.Search
    )
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Work.Search> {
                SearchScreen(
                    modifier = modifier,
                    viewModel = koinViewModel(),
                    onFound = { backStack.add(Route.Work.Found) }
                )
            }
            entry<Route.Work.Found> {
                //TODO: How to implement back navigation here???
                FoundScreen(modifier, koinViewModel())
            }
        }
    )
}