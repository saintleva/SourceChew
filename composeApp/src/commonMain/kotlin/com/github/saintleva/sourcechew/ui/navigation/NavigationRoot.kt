package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration


@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = serializersModule
        },
        Route.Search
    )
    NavDisplay(
        modifier = modifier,
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Search> {
                SearchNavigation()
            }
            entry<Route.Found> {
                FoundNavigation()
            }
            //TODO: Remove this
//            entry<Route.Auth> {
//                AuthNavigation(
//                    onLogin = {
//                        rootBackStack.remove(Route.Auth)
//                        rootBackStack.add(Route.Todo)
//                    }
//                )
//            }
//            entry<Route.Todo> {
//                TodoNavigation()
//            }
        }
    )
}