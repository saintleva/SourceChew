package com.github.saintleva.sourcechew

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.saintleva.sourcechew.di.initKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SourceChew",
    ) {
        initKoin()
        App()
    }
}