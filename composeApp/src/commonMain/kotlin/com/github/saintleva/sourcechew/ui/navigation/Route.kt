package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


@Serializable
sealed interface Route {

    @Serializable
    sealed interface Menu : Route {
        val icon: ImageVector?
        val contentDescription: String

        companion object {
            val entries = listOf(Authorization, Settings, About)
        }
    }

    @Serializable
    data object Authorization : Menu {
        override val icon: ImageVector = Icons.Default.Face
        override val contentDescription: String = "Authorization"
    }

    @Serializable
    data object Settings : Menu {
        override val icon: ImageVector = Icons.Default.Face
        override val contentDescription: String = "Authorization"
    }

    @Serializable
    data object About : Menu {
        override val icon: ImageVector = null
        override val contentDescription: String = "About"
    }

    @Serializable
    data object Search : Route

    @Serializable
    data object Found : Route
}

