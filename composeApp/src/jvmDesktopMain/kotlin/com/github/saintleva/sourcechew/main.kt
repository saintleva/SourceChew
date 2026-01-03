package com.github.saintleva.sourcechew

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.saintleva.sourcechew.di.initKoin
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatform.getKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SourceChew",
    ) {
        Napier.base(DebugAntilog())
        initKoin()

        val configRepository: ConfigRepository by getKoin().inject()
        runBlocking {
            configRepository.loadData()
        }

        App()
    }
}