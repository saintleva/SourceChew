package com.github.saintleva.sourcechew

import android.content.Context
import com.github.saintleva.sourcechew.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext


fun initApp(context: Context) {
    Napier.base(DebugAntilog())
    Napier.d(tag = "MyApplication") { "Napier initialized" }
    initKoin {
        androidContext(context)
    }
    Napier.d(tag = "MyApplication") { "Koin initialized" }
}