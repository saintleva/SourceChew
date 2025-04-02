package com.github.saintleva.sourcechew.domain.models

import org.koin.java.KoinJavaComponent.inject


interface StringDictConverter {
    fun toDict(string: String): Map<String, String>
    fun toString(map: Map<String, String>): String
}

sealed class QueryText {

    protected val stringDictConverter: StringDictConverter by inject(StringDictConverter::class.java)

    abstract fun asText(): Text
    abstract fun asDictionary(): Dictionary

    class Text(val text: String): QueryText() {

        override fun asText(): Text {
            return this
        }

        override fun asDictionary(): Dictionary {
            return Dictionary(stringDictConverter.toDict(text))
        }
    }

    class Dictionary(val dict: Map<String, String>): QueryText() {

        override fun asText(): Text {
            return Text(stringDictConverter.toString(dict))
        }

        override fun asDictionary(): Dictionary {
            return this
        }
    }
}