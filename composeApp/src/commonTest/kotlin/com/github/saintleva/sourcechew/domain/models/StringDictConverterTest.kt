package com.github.saintleva.sourcechew.domain.models

import com.github.saintleva.sourcechew.data.utils.StringDictConverterImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import com.github.saintleva.sourcechew.domain.models.StringDictConverter

class StringDictConverterTest {

    private val converter: StringDictConverter = StringDictConverterImpl()

//    private val converter: StringDictConverter = object : StringDictConverter {
//        override fun toDict(string: String): Map<String, String> {
//            return string.split(",").associate {
//                val (key, value) = it.split(":")
//                key to value
//            }
//        }
//
//        override fun toString(map: Map<String, String>): String {
//            return map.entries.joinToString(",") {
//                "${it.key}:${it.value}"
//            }
//        }
//    }

    @Test
    fun `StringDictConverter_toDict_converts_string_to_map`() {
        // Arrange
        val string = "key1:value1,key2:value2,key3:value3"
        val expectedMap = kotlin.collections.mapOf(
            "key1" to "value1",
            "key2" to "value2",
            "key3" to "value3"
        )

        // Act
        val result = converter.toDict(string)

        // Assert
        assertEquals(expectedMap, result)
    }

    @Test
    fun `StringDictConverter_toString_converts_map_to_string`() {
        // Arrange
        val map = kotlin.collections.mapOf(
            "key1" to "value1",
            "key2" to "value2",
            "key3" to "value3"
        )
        val expectedString = "key1:value1,key2:value2,key3:value3"

        // Act
        val result = converter.toString(map)

        // Assert
        assertEquals(expectedString, result)
    }
}