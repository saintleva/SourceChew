package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.saintleva.sourcechew.ui.screens.about.AboutApplicationScreen
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.about_application
import sourcechew.composeapp.generated.resources.authorization
import sourcechew.composeapp.generated.resources.go_back


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigableUpScreen(
    title: String,
    onBackClick: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.headlineMedium) },
                //modifier = appBarModifier, //TODO: Remove it
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = rootSerializersModule
        },
        Route.Work
    )
    NavDisplay(
        modifier = modifier,
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Menu.Authorization> {
                NavigableUpScreen(
                    stringResource(Res.string.authorization),
                    onBackClick = { rootBackStack.pop() }
                ) { innerPadding ->
                    AboutApplicationScreen(innerPadding)
                }
            }
            entry<Route.Menu.About> {
                NavigableUpScreen(
                    stringResource(Res.string.about_application),
                    onBackClick = { rootBackStack.pop() }
                ) { innerPadding ->
                    AboutApplicationScreen(innerPadding)
                }
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