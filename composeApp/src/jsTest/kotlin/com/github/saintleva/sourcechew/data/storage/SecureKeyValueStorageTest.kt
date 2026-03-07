package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.di.platformModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.mp.KoinPlatform.getKoin
import org.koin.test.KoinTest
import org.koin.test.inject


class SecureKeyValueStorageTest : FunSpec() {

    private val storage: SecureKeyValueStorage by lazy { getKoin().get() }

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

        test("test test") {
            1 shouldBe 2
        }
    }
}