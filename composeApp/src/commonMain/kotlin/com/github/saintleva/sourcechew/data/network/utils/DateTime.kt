package com.github.saintleva.sourcechew.data.network.utils

import com.github.saintleva.sourcechew.domain.models.DateTimeFilter
import com.github.saintleva.sourcechew.domain.models.Range
import kotlinx.datetime.LocalDate
import kotlin.jvm.JvmName
import kotlin.time.Instant

fun <T : Comparable<T>> Range<T>.toGitHubValue(
    format: (T) -> String = { it.toString() }
): String = when {
    from != null && to != null && from == to -> format(from)
    from != null && to != null -> "${format(from)}..${format(to)}"
    from != null -> ">=${format(from)}"
    to != null -> "<=${format(to)}"
    else -> ""
}

fun <T : Comparable<T>> Range<T>.toGitHubQuery(
    qualifier: String,
    format: (T) -> String = { it.toString() }
): String {
    val value = toGitHubValue(format)
    return if (value.isNotEmpty()) "$qualifier:$value" else ""
}

@JvmName("instantRangeToGitHubValue")
fun Range<Instant>.toGitHubValue(): String =
    toGitHubValue { formatInstant(it) }

@JvmName("localDateRangeToGitHubValue")
fun Range<LocalDate>.toGitHubValue(): String =
    toGitHubValue { it.toString() }

@JvmName("intRangeToGitHubValue")
fun Range<Int>.toGitHubValue(): String =
    toGitHubValue { it.toString() }

// Небольшой хелпер, чтобы обрезать миллисекунды у Instant, если они есть.
// Спецификация GitHub Search API ожидает формат YYYY-MM-DDTHH:MM:SSZ (без миллисекунд).
fun formatInstant(instant: Instant): String {
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
        is DateTimeFilter.Date ->
            filter.range.toGitHubQuery(qualifier) { it.toString() }

        is DateTimeFilter.Time ->
            filter.range.toGitHubQuery(qualifier) { formatInstant(it) }
    }
}
