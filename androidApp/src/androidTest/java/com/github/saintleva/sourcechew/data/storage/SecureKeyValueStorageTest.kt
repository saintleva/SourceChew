package com.github.saintleva.sourcechew.data.storage

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.github.saintleva.sourcechew.di.platformModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith
import org.koin.dsl.module


@RunWith(KotestTestRunner::class)
class AndroidSecureKeyValueStorageTest : FunSpec(), SecureKeyValueStorageTestShared {

    init {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        setupStorageTests(createPlatformModule(appContext))
    }

    companion object {
        fun createPlatformModule(externalContext: Context) = module {
            includes(platformModule)
            single<Context> { externalContext }
        }
    }
}