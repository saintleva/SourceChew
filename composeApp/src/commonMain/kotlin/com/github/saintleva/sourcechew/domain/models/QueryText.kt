package com.github.saintleva.sourcechew.domain.models

import org.koin.java.KoinJavaComponent.inject


interface StringDictConverter {
    fun toDict(string: String): Map<String, String>
    fun toString(map: Map<String, String>): String
}


sealed interface Matching {

    fun matches(value: String, pattern: String): Boolean

    object Exact: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            return value == pattern
        }

    }

    object Contains: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            return value.contains(pattern)
        }
    }
}

sealed interface Parameter {
    class Main(val value: String): Parameter
    class Pair(val key: String, val value: String): Parameter
}

class SearchParameter<out Param: Parameter>(
    val matching: Matching,
    val parameter: Param
)

class SearchQuery(
    val mainParameter: SearchParameter<Parameter.Main>,
    val parameters: List<SearchParameter<Parameter.Pair>>
) {
    //private val stringDictConverter: StringDictConverter by inject(StringDictConverter::class.java)

    constructor(
        mainParameter: SearchParameter<Parameter.Main>,
        vararg parameters: SearchParameter<Parameter.Pair>
    ): this(mainParameter, parameters.toList())
}

//sealed class QueryText {
//
//    protected val stringDictConverter: StringDictConverter by inject(StringDictConverter::class.java)
//
//    abstract fun asText(): Text
//    abstract fun asDictionary(): Dictionary
//
//    class Text(val text: String): QueryText() {
//
//        override fun asText(): Text {
//            return this
//        }
//
//        override fun asDictionary(): Dictionary {
//            return Dictionary(stringDictConverter.toDict(text))
//        }
//    }
//
//    class Dictionary(val dict: Map<String, String>): QueryText() {
//
//        override fun asText(): Text {
//            return Text(stringDictConverter.toString(dict))
//        }
//
//        override fun asDictionary(): Dictionary {
//            return this
//        }
//    }
//}