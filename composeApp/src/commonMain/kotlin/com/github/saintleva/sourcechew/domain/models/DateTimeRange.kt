package com.github.saintleva.sourcechew.domain.models

import kotlinx.datetime.LocalDate
import kotlin.time.Instant


data class Interval<T : Comparable<T>>(
    val start: Endpoint<T>? = null,
    val end: Endpoint<T>? = null,
) {

    init {
        require(start != null || end != null) {
            "Interval must have at least one boundary (start or end)."
        }

        if (start != null && end != null) {
            require(start.value <= end.value) {
                "Interval start must be <= end."
            }
        }
    }

    companion object {

        fun <T : Comparable<T>> exactly(value: T): Interval<T> =
            Interval(
                start = Endpoint(value, inclusive = true),
                end = Endpoint(value, inclusive = true)
            )

        fun <T : Comparable<T>> atLeast(value: T): Interval<T> =
            Interval(
                start = Endpoint(value, inclusive = true),
                end = null
            )

        fun <T : Comparable<T>> greaterThan(value: T): Interval<T> =
            Interval(
                start = Endpoint(value, inclusive = false),
                end = null
            )

        fun <T : Comparable<T>> atMost(value: T): Interval<T> =
            Interval(
                start = null,
                end = Endpoint(value, inclusive = true)
            )

        fun <T : Comparable<T>> lessThan(value: T): Interval<T> =
            Interval(
                start = null,
                end = Endpoint(value, inclusive = false)
            )

        fun <T : Comparable<T>> between(
            start: T,
            end: T,
            startInclusive: Boolean = true,
            endInclusive: Boolean = false,
        ): Interval<T> =
            Interval(
                start = Endpoint(start, startInclusive),
                end = Endpoint(end, endInclusive)
            )
    }
}

data class Endpoint<T : Comparable<T>>(
    val value: T,
    val inclusive: Boolean = true
)

sealed interface DateTimeFilter {

    data class ExactDate(
        val value: LocalDate
    ) : DateTimeFilter

    data class ExactInstant(
        val value: Instant
    ) : DateTimeFilter

    data class DateInterval(
        val value: Interval<LocalDate>
    ) : DateTimeFilter

    data class InstantInterval(
        val value: Interval<Instant>
    ) : DateTimeFilter
}