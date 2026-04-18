package com.github.saintleva.sourcechew

import android.app.Application
import com.github.saintleva.sourcechew.androidapp.BuildConfig

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initApp(isDebug = BuildConfig.DEBUG, context = this)
    }
}