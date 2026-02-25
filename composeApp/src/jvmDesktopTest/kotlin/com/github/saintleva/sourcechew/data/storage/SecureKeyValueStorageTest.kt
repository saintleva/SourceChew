package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.di.appModule
import com.github.saintleva.sourcechew.di.domainModule
import com.github.saintleva.sourcechew.di.networkModule
import com.github.saintleva.sourcechew.di.platformModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject


class SecureKeyValueStorageTest : FunSpec(), KoinTest {

    private val storage: SecureKeyValueStorage by inject()

    init {
        extension(KoinExtension(platformModule))

        test("test write, read, and remove") {
            storage.write("test_key_1", "test_value_1")
            storage.write("test_key_2", "test_value_2")
            storage.write("test_key_3", "test_value_3")

            storage.read("test_key_1") shouldBe "test_value_1"
            storage.read("test_key_2") shouldBe "test_value_2"
            storage.read("test_key_3") shouldBe "test_value_3"

            storage.read("unexisting_key") shouldBe null

            storage.remove("test_key_1")
            storage.read("test_key_1") shouldBe null
            storage.read("test_key_2") shouldBe "test_value_2"
        }
    }
}