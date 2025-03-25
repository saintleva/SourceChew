package com.github.saintleva.sourcechew

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.github.saintleva.sourcechew.ui.screens.search.SearchScreen
import com.github.saintleva.sourcechew.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    AppTheme {
        KoinContext {
            Navigator(SearchScreen())
        }
    }
}