package com.github.saintleva.sourcechew

import androidx.compose.runtime.Composable
import com.github.saintleva.sourcechew.ui.navigation.NavigationRoot
import com.github.saintleva.sourcechew.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    AppTheme {
        NavigationRoot()
    }
}