package com.github.saintleva.sourcechew.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


@Serializable
sealed interface Route : NavKey {

    @Serializable
    sealed interface Menu : Route {

        @Serializable
        data object Authorization : Menu
        @Serializable
        data object Settings : Menu
        @Serializable
        data object About : Menu

        companion object {
            val entries = listOf<Menu>(Authorization, Settings, About)
        }
    }

    @Serializable
    sealed interface Work : Route {

        @Serializable
        data object Search : Work

        @Serializable
        data object Found : Work
    }
}

val rootSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Route.Menu::class, Route.Menu.serializer()) //TODO: Do I really need this?
        subclass(Route.Menu.Authorization::class, Route.Menu.Authorization.serializer())
        subclass(Route.Menu.Settings::class, Route.Menu.Settings.serializer())
        subclass(Route.Menu.About::class, Route.Menu.About.serializer())
        subclass(Route.Work.Search::class, Route.Work.Search.serializer())
        subclass(Route.Work.Found::class, Route.Work.Found.serializer())
    }
}

val workSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Route.Work.Search::class, Route.Work.Search.serializer())
        subclass(Route.Work.Found::class, Route.Work.Found.serializer())
    }
}