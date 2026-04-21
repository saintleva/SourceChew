package com.github.saintleva.sourcechew

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.github.saintleva.sourcechew.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    if (BuildKonfig.IS_DEBUG) Napier.base(DebugAntilog())
    initKoin()
    ComposeViewport {
        App()
    }
}