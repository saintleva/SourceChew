package com.github.saintleva.sourcechew.data.network.utils

import com.github.saintleva.sourcechew.domain.models.Range
import kotlinx.datetime.LocalDate
import kotlin.jvm.JvmName

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

@JvmName("localDateRangeToGitHubValue")
fun Range<LocalDate>.toGitHubValue(): String =
    toGitHubValue { it.toString() }

@JvmName("intRangeToGitHubValue")
fun Range<Int>.toGitHubValue(): String =
    toGitHubValue { it.toString() }

fun parseInstant(isoString: String?): kotlin.time.Instant? =
    isoString?.let {
        try {
            kotlin.time.Instant.parse(it)
        } catch (_: Exception) {
            null
        }
    }

