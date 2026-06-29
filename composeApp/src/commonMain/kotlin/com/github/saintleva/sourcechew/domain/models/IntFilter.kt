package com.github.saintleva.sourcechew.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class IntFilter(
    val value: Int,
    val operator: Operator
) {
    enum class Operator {
        GT,  // >
        LT,  // <
        GTE, // >=
        LTE  // <=
    }

    init {
        require(value > 0) { "Value must be positive" }
    }
}