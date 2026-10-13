package com.github.saintleva.sourcechew.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Range<T : Comparable<T>>(
    val from: T? = null,
    val to: T? = null,
) {
    init {
        require(from != null || to != null) {
            "Range must have at least one boundary (from or to)."
        }
        if (from != null && to != null) {
            require(from <= to) {
                "Range 'from' ($from) must be <= 'to' ($to)."
            }
        }
    }

    val isExact: Boolean
        get() = from != null && to != null && from == to

    companion object {
        fun <T : Comparable<T>> exact(value: T): Range<T> =
            Range(from = value, to = value)

        fun <T : Comparable<T>> atLeast(from: T): Range<T> =
            Range(from = from, to = null)

        fun <T : Comparable<T>> atMost(to: T): Range<T> =
            Range(from = null, to = to)

        fun <T : Comparable<T>> between(from: T, to: T): Range<T> =
            Range(from = from, to = to)
    }
}
