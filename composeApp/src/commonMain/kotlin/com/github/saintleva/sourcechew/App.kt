package com.github.saintleva.sourcechew

import androidx.compose.runtime.Composable
import com.github.saintleva.sourcechew.ui.navigation.AppScaffold
import com.github.saintleva.sourcechew.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    AppTheme {
        AppScaffold()
        }
    }
}