package com.github.saintleva.sourcechew

import android.app.Application
import com.github.saintleva.sourcechew.di.initKoin

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initApp(this)
    }
}