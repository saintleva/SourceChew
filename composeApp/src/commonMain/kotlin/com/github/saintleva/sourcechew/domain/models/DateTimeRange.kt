package com.github.saintleva.sourcechew.domain.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed interface DateTimeFilter {

    @Serializable
    data class Date(
        val range: Range<LocalDate>
    ) : DateTimeFilter

    @Serializable
    data class Time(
        val range: Range<Instant>
    ) : DateTimeFilter
}
