package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


@Serializable
sealed interface Route : NavKey {

    @Serializable
    sealed interface Menu : Route {
        val icon: ImageVector?
        val label: String

        @Serializable
        data object Authorization : Menu {
            override val icon = Icons.Default.Face
            override val label = "Authorization"
        }

        @Serializable
        data object Settings : Menu {
            override val icon = Icons.Default.Settings
            override val label = "Settings"
        }

        @Serializable
        data object About : Menu {
            override val icon = null
            override val label = "About"
        }
        companion object {
            val entries = listOf(Authorization, Settings, About)
        }
    }

    @Serializable
    data object Work : Route {

        @Serializable
        data object Search : Route

        @Serializable
        data object Found : Route
    }
}

val rootSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Route.Menu::class, Route.Menu.serializer()) //TODO: Do I really need this?
        subclass(Route.Menu.Authorization::class, Route.Menu.Authorization.serializer())
        subclass(Route.Menu.Settings::class, Route.Menu.Settings.serializer())
        subclass(Route.Menu.About::class, Route.Menu.About.serializer())
        subclass(Route.Work::class, Route.Work.serializer())

}

val workSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Route.Work.Search::class, Route.Work.Search.serializer())
        subclass(Route.Work.Found::class, Route.Work.Found.serializer())
    }
}