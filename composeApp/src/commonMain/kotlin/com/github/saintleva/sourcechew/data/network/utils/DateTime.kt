package com.github.saintleva.sourcechew.data.network.utils

import com.github.saintleva.sourcechew.domain.models.DateTimeFilter
import com.github.saintleva.sourcechew.domain.models.Interval
import kotlinx.datetime.LocalDate
import kotlin.time.Instant


fun <T : Comparable<T>> Interval<T>.toGitHubQuery(
    qualifier: String,
    format: (T) -> String
): String {
    if (start != null && end != null && start.inclusive && end.inclusive) {
        return "$qualifier:${format(start.value)}..${format(end.value)}"
    }

    val parts = buildList {
        start?.let {
            val op = if (it.inclusive) ">=" else ">"
            add("$qualifier:$op${format(it.value)}")
        }

        end?.let {
            val op = if (it.inclusive) "<=" else "<"
            add("$qualifier:$op${format(it.value)}")
        }
    }

    return parts.joinToString(" ")
}

fun Interval<Instant>.toGitHubQuery(qualifier: String): String =
    toGitHubQuery(qualifier) { formatInstant(it) }

fun Interval<LocalDate>.toGitHubQuery(qualifier: String): String =
    toGitHubQuery(qualifier) { it.toString() }

// Небольшой хелпер, чтобы обрезать миллисекунды у Instant, если они есть.
// Спецификация GitHub Search API ожидает формат YYYY-MM-DDTHH:MM:SSZ (без миллисекунд).
private fun formatInstant(instant: Instant): String {
    val str = instant.toString() // Может вернуть 2023-10-27T10:15:30.123Z
    val dotIndex = str.indexOf('.')
    if (dotIndex != -1) {
        val zIndex = str.indexOf('Z', dotIndex)
        if (zIndex != -1) {
            return str.substring(0, dotIndex) + "Z"
        }
    }
    return str
}

object GitHubDateTimeQualifier {

    fun build(filter: DateTimeFilter, qualifier: String = "created"): String = when (filter) {
        is DateTimeFilter.ExactDate ->
            "$qualifier:${filter.value}"

        is DateTimeFilter.ExactInstant ->
            "$qualifier:${formatInstant(filter.value)}"

        is DateTimeFilter.DateInterval ->
            filter.value.toGitHubQuery(qualifier)

        is DateTimeFilter.InstantInterval ->
            filter.value.toGitHubQuery(qualifier)
    }
}