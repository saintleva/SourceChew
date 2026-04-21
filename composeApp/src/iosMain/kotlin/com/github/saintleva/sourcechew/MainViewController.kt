package com.github.saintleva.sourcechew

import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun MainViewController() = ComposeUIViewController {
    if (BuildKonfig.IS_DEBUG) Napier.base(DebugAntilog())
    App()
}