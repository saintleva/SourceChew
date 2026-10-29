package com.github.saintleva.sourcechew.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.ui.screens.search.SearchTab
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
        data object WorkRoot : Work

        @Serializable
        data class Search(val tab: SearchTab = SearchTab.REPO) : Work

        sealed interface Found : Work {

            @Serializable
            data object Repo : Found

            @Serializable
            data object Owner : Found
        }

        @Serializable
        data class RepoDetails(val owner: String, val name: String) : Work {
            val repoRef: RepoRef get() = RepoRef(owner, name)
        }

        @Serializable
        data class OwnerDetails(val login: String) : Work

        @Serializable
        data class Branches(
            val owner: String,
            val repo: String,
            val defaultBranch: String? = null
        ) : Work {
            val repoRef: RepoRef get() = RepoRef(owner, repo)
        }
    }
}

val rootSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Route.Menu::class, Route.Menu.serializer()) //TODO: Do I really need this?
        subclass(Route.Menu.Authorization::class, Route.Menu.Authorization.serializer())
        subclass(Route.Menu.Settings::class, Route.Menu.Settings.serializer())
        subclass(Route.Menu.About::class, Route.Menu.About.serializer())
        subclass(Route.Work.WorkRoot::class, Route.Work.WorkRoot.serializer())
    }
}

val workSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Route.Work.WorkRoot::class, Route.Work.WorkRoot.serializer())
        subclass(Route.Work.Search::class, Route.Work.Search.serializer())
        subclass(Route.Work.Found.Repo::class, Route.Work.Found.Repo.serializer())
        subclass(Route.Work.Found.Owner::class, Route.Work.Found.Owner.serializer())
        subclass(Route.Work.RepoDetails::class, Route.Work.RepoDetails.serializer())
        subclass(Route.Work.OwnerDetails::class, Route.Work.OwnerDetails.serializer())
        subclass(Route.Work.Branches::class, Route.Work.Branches.serializer())
    }
}