package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.saintleva.sourcechew.ui.screens.about.AboutApplicationScreen
import com.github.saintleva.sourcechew.ui.screens.auth.AuthScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.about_application
import sourcechew.composeapp.generated.resources.authorization


@Composable
fun TitledNavigableBackScreen(
    title: String,
    onBackClick: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    NavigableBackScreen(
        title = { Text(title, style = MaterialTheme.typography.headlineMedium) },
        onBackClick = onBackClick
    ) { innerPadding ->
        content(innerPadding)
    }
}


@Composable
fun NavigationRoot() {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = rootSerializersModule
        },
        Route.Work.Search
    )
    NavDisplay(
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Work> {
                WorkNavigation(onMenuItemClick = { rootBackStack.add(it) } )
            }
            entry<Route.Menu.Authorization> {
                TitledNavigableBackScreen(
                    stringResource(Res.string.authorization),
                    onBackClick = { rootBackStack.pop() }
                ) { innerPadding ->
                    AuthScreen(innerPadding, koinViewModel())
                }
            }
            entry<Route.Menu.Settings> {
                //TODO: Implement settings screen and navigation
//                TitledNavigableBackScreen(
//                    stringResource(Res.string.about_application),
//                    onBackClick = { rootBackStack.pop() }
//                ) { innerPadding ->
//                    AboutApplicationScreen(innerPadding)
//                }
            }
            entry<Route.Menu.About> {
                TitledNavigableBackScreen(
                    stringResource(Res.string.about_application),
                    onBackClick = { rootBackStack.pop() }
                ) { innerPadding ->
                    AboutApplicationScreen(innerPadding)
                }
            }
        }
    )
}