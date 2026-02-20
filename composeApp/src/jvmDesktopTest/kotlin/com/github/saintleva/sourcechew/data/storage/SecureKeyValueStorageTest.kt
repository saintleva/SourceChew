package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.di.appModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class SecureKeyValueStorageTest : FunSpec(), KoinTest {

    private val storage: SecureKeyValueStorage by inject()

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        startKoin { modules(appModule) }
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        stopKoin()
    }

    init {
        test("test write, read, and remove") {
            val key = "test_key"
            val value = "test_value"

            // Write and read
            storage.write(key, value)
            storage.read(key) shouldBe value

            // Remove
            storage.remove(key)
            storage.read(key) shouldBe null
        }
    }
}