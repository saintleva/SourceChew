package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.di.platformModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class JvmDesktopSecureKeyValueStorageTest : FunSpec(), SecureKeyValueStorageTestShared {
    init {
        setupStorageTests(platformModule)
    }
}
