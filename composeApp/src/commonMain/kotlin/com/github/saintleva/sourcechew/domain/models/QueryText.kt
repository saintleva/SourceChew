package com.github.saintleva.sourcechew.domain.models


sealed interface Matching {

    fun matches(value: String, pattern: String): Boolean

    object Exact: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            return value == pattern
        }
    }

    object NotEqual: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            return value != pattern
        }
    }

    object LessThan: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            //TODO: Implement it
            return false
        }
    }

    object GreaterThan: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            //TODO: Implement it
            return false
        }
    }

    object LessOrEqual: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            return !GreaterThan.matches(value, pattern)
        }
    }

    object GreaterOrEqual: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            return !LessThan.matches(value, pattern)
        }
    }

    object Contains: Matching {
        override fun matches(value: String, pattern: String): Boolean {
            return value.contains(pattern)
        }
    }

    object NotContains: Matching {
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
    val mainParameter: SearchParameter<Parameter.Main>?,
    val parameters: List<SearchParameter<Parameter.Pair>>
) {
    constructor(
        mainParameter: SearchParameter<Parameter.Main>?,
        vararg parameters: SearchParameter<Parameter.Pair>
    ): this(mainParameter, parameters.toList())
}