package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.di.platformModule
import io.kotest.core.spec.style.FunSpec

class WasmJsSecureKeyValueStorageTest : FunSpec(), SecureKeyValueStorageTestShared {
    init {
        setupStorageTests(platformModule)
    }
}
