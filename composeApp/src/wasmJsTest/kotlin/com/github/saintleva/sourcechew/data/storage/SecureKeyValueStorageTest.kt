package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.di.appModule
import com.github.saintleva.sourcechew.di.platformModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject


class WasmJsSecureKeyValueStorageTest : AbstractSecureKeyValueStorageTest(platformModule)