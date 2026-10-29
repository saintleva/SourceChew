package com.github.saintleva.sourcechew.ui.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.time_day_ago
import sourcechew.composeapp.generated.resources.time_days_ago
import sourcechew.composeapp.generated.resources.time_hour_ago
import sourcechew.composeapp.generated.resources.time_hours_ago
import sourcechew.composeapp.generated.resources.time_just_now
import sourcechew.composeapp.generated.resources.time_minute_ago
import sourcechew.composeapp.generated.resources.time_minutes_ago
import sourcechew.composeapp.generated.resources.time_month_ago
import sourcechew.composeapp.generated.resources.time_months_ago
import sourcechew.composeapp.generated.resources.time_year_ago
import sourcechew.composeapp.generated.resources.time_years_ago
import kotlin.math.round
import kotlin.time.Clock
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

fun currentSystemInstant(): Instant = Clock.System.now()


/**
 * Formats count into compact representation (e.g., 1700 -> "1.7k", 12300 -> "12.3k").
 */
fun formatCompactNumber(count: Int): String {
    if (count < 1000) return count.toString()
    if (count < 1_000_000) {
        val thousands = count / 1000.0
        return if (thousands >= 100.0) {
            "${count / 1000}k"
        } else {
            val rounded = round(thousands * 10) / 10.0
            val text = rounded.toString()
            if (text.endsWith(".0")) "${text.substringBefore(".0")}k" else "${text}k"
        }
    }
    val millions = count / 1_000_000.0
    return if (millions >= 100.0) {
        "${count / 1_000_000}M"
    } else {
        val rounded = round(millions * 10) / 10.0
        val text = rounded.toString()
        if (text.endsWith(".0")) "${text.substringBefore(".0")}M" else "${text}M"
    }
}

/**
 * Non-composable pure relative time formatter fallback (e.g. for unit tests).
 */
fun Instant.toRelativeTimeText(
    now: Instant = currentSystemInstant()
): String {
    val duration = now - this
    val seconds = duration.inWholeSeconds
    if (seconds < 60) return "just now"
    val minutes = duration.inWholeMinutes
    if (minutes < 60) return if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
    val hours = duration.inWholeHours
    if (hours < 24) return if (hours == 1L) "1 hour ago" else "$hours hours ago"
    val days = duration.inWholeDays
    if (days < 30) return if (days == 1L) "1 day ago" else "$days days ago"
    val months = days / 30
    if (months < 12) return if (months == 1L) "1 month ago" else "$months months ago"
    val years = days / 365
    return if (years == 1L) "1 year ago" else "$years years ago"
}

/**
 * Composable relative time formatter using localized resources.
 */
@Composable
fun Instant.formatRelativeTime(
    now: Instant = currentSystemInstant()
): String {
    val duration = now - this
    val seconds = duration.inWholeSeconds
    if (seconds < 60) {
        return stringResource(Res.string.time_just_now)
    }
    val minutes = duration.inWholeMinutes
    if (minutes < 60) {
        return if (minutes == 1L) {
            stringResource(Res.string.time_minute_ago)
        } else {
            stringResource(Res.string.time_minutes_ago, minutes.toInt())
        }
    }
    val hours = duration.inWholeHours
    if (hours < 24) {
        return if (hours == 1L) {
            stringResource(Res.string.time_hour_ago)
        } else {
            stringResource(Res.string.time_hours_ago, hours.toInt())
        }
    }
    val days = duration.inWholeDays
    if (days < 30) {
        return if (days == 1L) {
            stringResource(Res.string.time_day_ago)
        } else {
            stringResource(Res.string.time_days_ago, days.toInt())
        }
    }
    val months = days / 30
    if (months < 12) {
        return if (months == 1L) {
            stringResource(Res.string.time_month_ago)
        } else {
            stringResource(Res.string.time_months_ago, months.toInt())
        }
    }
    val years = days / 365
    return if (years == 1L) {
        stringResource(Res.string.time_year_ago)
    } else {
        stringResource(Res.string.time_years_ago, years.toInt())
    }
}

