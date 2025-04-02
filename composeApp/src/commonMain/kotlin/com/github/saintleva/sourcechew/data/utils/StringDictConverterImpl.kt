package com.github.saintleva.sourcechew.data.utils

import com.github.saintleva.sourcechew.domain.models.StringDictConverter


class StringDictConverterImpl : StringDictConverter {

    override fun toDict(string: String): Map<String, String> {
        return mapOf(
            "key1" to "value1",
            "key2" to "value2",
            "key3" to "value3"
        )
    }

    override fun toString(map: Map<String, String>): String {
        return map.entries.joinToString(separator = " ", prefix = "{", postfix = "}")
    }
}