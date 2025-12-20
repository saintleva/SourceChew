package com.github.saintleva.sourcechew

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.saintleva.sourcechew.ui.navigation.NavigationRoot
import com.github.saintleva.sourcechew.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    AppTheme {
        NavigationRoot(Modifier)
    }
}