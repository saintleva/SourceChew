package com.github.saintleva.sourcechew.domain.models


sealed interface Matching {

    val designation: String
    fun matches(value: String, pattern: String): Boolean

    data object Exact: Matching {
        override val designation: String = "="
        override fun matches(value: String, pattern: String): Boolean {
            return value == pattern
        }
    }

    data object NotEqual: Matching {
        override val designation: String = "!="
        override fun matches(value: String, pattern: String): Boolean {
            return value != pattern
        }
    }

    data object LessThan: Matching {
        override val designation: String = "<"
        override fun matches(value: String, pattern: String): Boolean {
            //TODO: Implement it
            return false
        }
    }

    data object GreaterThan: Matching {
        override val designation: String = ">"
        override fun matches(value: String, pattern: String): Boolean {
            //TODO: Implement it
            return false
        }
    }

    data object LessOrEqual: Matching {
        override val designation: String = "<="
        override fun matches(value: String, pattern: String): Boolean {
            return !GreaterThan.matches(value, pattern)
        }
    }

    data object GreaterOrEqual: Matching {
        override val designation: String = ">="
        override fun matches(value: String, pattern: String): Boolean {
            return !LessThan.matches(value, pattern)
        }
    }

    data object Contains: Matching {
        override val designation: String = "~"
        override fun matches(value: String, pattern: String): Boolean {
            return value.contains(pattern)
        }
    }

    data object NotContains: Matching {
        override val designation: String = "!~"
        override fun matches(value: String, pattern: String): Boolean {
            return value.contains(pattern)
        }
    }

    companion object {

        val exact = setOf(Exact)
        val contains = setOf(Contains)
        val equaling = setOf(Exact, NotEqual)
        val comparisons = setOf(Exact, NotEqual, LessThan, GreaterThan, LessOrEqual, GreaterOrEqual)

        val byDesignation = mapOf("=" to Exact, "!=" to NotEqual, "<" to LessThan, ">" to GreaterThan,
            "<=" to LessOrEqual, ">=" to GreaterOrEqual, "~" to Contains, "!~" to NotContains)
    }
}

sealed class Parameter(val value: String) {
    class Main(value: String): Parameter(value)
    class Pair(val key: String, value: String): Parameter(value)
}

class SearchParameter<out Param: Parameter>(
    val parameter: Param,
    val matching: Matching
)

class SearchQuery(
    val mainParameter: SearchParameter<Parameter.Main>? = null,
    val parameters: List<SearchParameter<Parameter.Pair>> = emptyList()
) {
    constructor(
        mainParameter: SearchParameter<Parameter.Main>?,
        vararg parameters: SearchParameter<Parameter.Pair>
    ): this(mainParameter, parameters.toList())
}

fun SearchQuery.isEmpty() = mainParameter == null && parameters.isEmpty()
fun SearchQuery.isNotEmpty() = !isEmpty()