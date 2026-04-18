package com.github.saintleva.sourcechew

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.saintleva.sourcechew.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() {
    Napier.base(DebugAntilog())
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SourceChew",
        ) {
            App()
        }
    }
}