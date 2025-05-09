package com.github.saintleva.sourcechew.data.utils

import io.kotest.core.spec.style.FunSpec


class StringDictConverterImplTest : FunSpec({

    // Создаём экземпляр класса, который будем тестировать
    val converter = StringDictConverterImpl() // Замени на создание твоего конвертера

    // Начинаем описывать тесты с помощью ключевого слова "test"
    test("convertToDict should correctly convert a valid string") {
        // Подготавливаем входные данные
        val inputString = "key1:value1;key2:value2"

        // Вызываем метод, который тестируем
        val result = converter.convertToDict(inputString)

        // Проверяем результат с помощью ассершенов Kotest
        result shouldBe mapOf("key1" to "value1", "key2" to "value2")
    }

    test("convertToDict should handle an empty string") {
        val inputString = ""
        val result = converter.convertToDict(inputString)
        result shouldBe emptyMap()
    }

    test("convertToDict should handle a string with only delimiters") {
        val inputString = ";;"
        val result = converter.convertToDict(inputString)
        result shouldBe emptyMap()
    }

    // Добавь другие тесты по мере необходимости
})