package com.github.saintleva.sourcechew.ui.common

import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Formats a [kotlin.time.Instant] into a human-readable date-time string
 * in the format `YYYY-MM-DD HH:mm` using the system default time zone.
 */
fun Instant.formatDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = this.toLocalDateTime(timeZone)
    val year = localDateTime.year
    val month = (localDateTime.month.ordinal + 1).toString().padStart(2, '0')
    val day = localDateTime.day.toString().padStart(2, '0')
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$year-$month-$day $hour:$minute"
}
