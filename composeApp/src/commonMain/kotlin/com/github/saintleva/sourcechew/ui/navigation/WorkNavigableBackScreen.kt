package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.app_name
import sourcechew.composeapp.generated.resources.menu


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkNavigableBackScreen(
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedMenuItem = rememberSaveable { mutableIntStateOf(0) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Route.Menu.entries.forEach { route ->
                    NavigationDrawerItem(
                        label = { Text(route.label) },
                        selected = selectedMenuItem.value == Route.Menu.entries.indexOf(route),
                        icon = { route.icon?.let {
                            Icon(it, contentDescription = route.label) } },
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedMenuItem.value = Route.Menu.entries.indexOf(route)
                        }
                    )

                }
            }
        }
    ) {
        Scaffold(
            //contentWindowInsets = WindowInsets.systemBars, //TODO: Remove this
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(Res.string.menu)
                            )
                        }
                    },
                    actions = actions
                    //windowInsets = TopAppBarDefaults.windowInsets //TODO: Remove this
                )
            }
        ) { innerPadding ->
            content(
                Modifier.padding(innerPadding)
            )
        }
    }

}