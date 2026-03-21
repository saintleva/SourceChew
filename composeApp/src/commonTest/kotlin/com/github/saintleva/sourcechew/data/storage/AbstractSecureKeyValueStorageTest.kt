package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.ClearableSecureKeyValueStorage
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.inject

@Ignored
abstract class AbstractSecureKeyValueStorageTest protected constructor(
    platformModuleForTest: Module
) : FunSpec(), KoinTest {

    private val storage: SecureKeyValueStorage by inject()

    private suspend fun clearStorage() {
        val s = storage
        if (s is ClearableSecureKeyValueStorage) s.clearAll()
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        clearStorage()
    }

    init {
        extension(KoinExtension(platformModuleForTest))

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

        test("test overwriting existing key") {
            val key = "overwrite_key"
            storage.write(key, "first_value")
            storage.read(key) shouldBe "first_value"

            storage.write(key, "second_value")
            storage.read(key) shouldBe "second_value"
        }

        test("test special characters and unicode") {
            val keys = listOf("key with spaces", "ключ_на_кириллице", "🚀_emoji_key")
            val values = listOf("value with \n newline", "значение", "🔥_emojis_and_symbols_!@#$%^&*()")

            keys.forEachIndexed { index, key ->
                storage.write(key, values[index])
                storage.read(key) shouldBe values[index]
            }
        }

        test("test empty strings") {
            // Ключ не пустой, но значение пустое
            storage.write("empty_val_key", "")
            storage.read("empty_val_key") shouldBe ""

            // Пустой ключ (если платформа позволяет)
            storage.write("", "empty_key_val")
            storage.read("") shouldBe "empty_key_val"
        }

        test("test large value") {
            val largeValue = "a".repeat(10_000) // 10KB токен - вполне реально для JWT
            storage.write("large_key", largeValue)
            storage.read("large_key") shouldBe largeValue
        }

        test("test clearAll if supported") {
            if (storage is ClearableSecureKeyValueStorage) {
                storage.write("test_key_1", "test_value_1")
                storage.write("test_key_2", "test_value_2")
                storage.write("test_key_3", "test_value_3")
                clearStorage()
                storage.read("test_key_1") shouldBe null
                storage.read("test_key_2") shouldBe null
                storage.read("test_key_3") shouldBe null
            } else {
                println("clearAll is not supported on this platform, skipping test")
            }
        }
    }
}
