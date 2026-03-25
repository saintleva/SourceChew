package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.ClearableSecureKeyValueStorage
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.inject

interface SecureKeyValueStorageTestShared : KoinTest {

    fun FunSpec.setupStorageTests(platformModuleForTest: Module) {
        val storage: SecureKeyValueStorage by inject()

        suspend fun clearStorage() {
            val s = storage
            if (s is ClearableSecureKeyValueStorage) s.clearAll()
        }

        afterTest {
            clearStorage()
        }

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
            storage.write("empty_val_key", "")
            storage.read("empty_val_key") shouldBe ""

            storage.write("", "empty_key_val")
            storage.read("") shouldBe "empty_key_val"
        }

        test("test large value") {
            val largeValue = "a".repeat(10_000)
            storage.write("large_key", largeValue)
            storage.read("large_key") shouldBe largeValue
        }

        test("test clearAll if supported") {
            val s = storage
            if (s is ClearableSecureKeyValueStorage) {
                s.write("test_key_1", "test_value_1")
                s.write("test_key_2", "test_value_2")
                s.write("test_key_3", "test_value_3")
                clearStorage()
                s.read("test_key_1") shouldBe null
                s.read("test_key_2") shouldBe null
                s.read("test_key_3") shouldBe null
            } else {
                println("clearAll is not supported on this platform, skipping test")
            }
        }
    }
}
