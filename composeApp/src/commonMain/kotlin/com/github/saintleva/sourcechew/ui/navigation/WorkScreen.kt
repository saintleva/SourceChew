package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.about
import sourcechew.composeapp.generated.resources.app_name
import sourcechew.composeapp.generated.resources.authorization
import sourcechew.composeapp.generated.resources.menu
import sourcechew.composeapp.generated.resources.search
import sourcechew.composeapp.generated.resources.settings


data class MenuItemUi(
    val route: Route,
    val label: StringResource,
    val icon: ImageVector?
)

fun Route.Menu.toUiItem(): MenuItemUi {
    return when (this) {
        //TODO: Use good icon for authorization
        Route.Menu.Authorization -> MenuItemUi(this, Res.string.authorization, Icons.Default.Face)
        Route.Menu.Settings -> MenuItemUi(this, Res.string.settings, Icons.Default.Settings)
        Route.Menu.About -> MenuItemUi(this, Res.string.about, null)
    }
}

//TODO: Remove this
//val menuItems = Route.Menu.entries.map { it.toUiItem() } +
//        MenuItemUi(Route.Work.Search, Res.string.search, Icons.Default.Search)


//TODO: Remove this
//fun Route.Work.Search.toUiItem(): MenuItemUi {
//    return MenuItemUi(this, Res.string.search, Icons.Default.Search)
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkScreen(
    actions: @Composable RowScope.() -> Unit = {},
    onSearchItemClick: () -> Unit = {},
    onMenuItemClick: (Route.Menu) -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //TODO: Remove this selection handling
    val selectedMenuItem = retain { mutableStateOf<Route>(Route.Work.Search) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(stringResource(Res.string.search)) },
                    selected = selectedMenuItem.value == Route.Work.Search,
                    icon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(Res.string.search)
                        )
                    },
                    onClick = {
                        onSearchItemClick()
                        scope.launch { drawerState.close() }
                        selectedMenuItem.value = Route.Work.Search
                    }
                )
                HorizontalDivider()
                Route.Menu.entries.forEach { route ->
                    val uiItem = route.toUiItem()
                    NavigationDrawerItem(
                        label = { Text(stringResource(uiItem.label)) },
                        selected = selectedMenuItem.value == route,
                        icon = {
                            uiItem.icon?.let {
                                Icon(it, contentDescription = stringResource(uiItem.label))
                            }
                        },
                        onClick = {
                            onMenuItemClick(route)
                            scope.launch { drawerState.close() }
                            selectedMenuItem.value = route
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
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
                )
            }
        ) { innerPadding ->
            content(
                Modifier.padding(innerPadding)
            )
        }
    }

}