package com.github.saintleva.sourcechew.ui.navigation

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
import com.github.saintleva.sourcechew.ui.screens.found.FoundScreen
import com.github.saintleva.sourcechew.ui.screens.search.SearchScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.about_application
import sourcechew.composeapp.generated.resources.authorization


@Composable
fun WorkNavigation(
    modifier: Modifier = Modifier
) {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = workSerializersModule
        },
        Route.Work.Search
    )
    NavDisplay(
        modifier = modifier,
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Work.Search> {
                SearchScreen(modifier, koinViewModel())
            }
            entry<Route.Work.Found> {
                FoundScreen(modifier, koinViewModel())
            }
        }
    )