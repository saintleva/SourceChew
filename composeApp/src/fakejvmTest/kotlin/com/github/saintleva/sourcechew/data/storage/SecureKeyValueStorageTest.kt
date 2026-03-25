package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.di.platformModule
import io.kotest.core.spec.style.FunSpec


class FakejvmSecureKeyValueStorageTest : FunSpec(), SecureKeyValueStorageTestShared {
    init {
        setupStorageTests(platformModule)
    }
}