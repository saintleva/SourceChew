package com.github.saintleva.sourcechew.data.storage

import androidx.test.platform.app.InstrumentationRegistry
import com.github.saintleva.sourcechew.di.createPlatformModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith


@RunWith(KotestTestRunner::class)
class AndroidSecureKeyValueStorageTest : FunSpec(), SecureKeyValueStorageTestShared {
    init {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        setupStorageTests(createPlatformModule(appContext))
    }
}