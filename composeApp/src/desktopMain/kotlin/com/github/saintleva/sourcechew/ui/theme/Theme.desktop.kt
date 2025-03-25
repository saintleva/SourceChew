package com.github.saintleva.sourcechew.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkScheme else lightScheme,
        typography = AppTypography,
        content = content
    )
}