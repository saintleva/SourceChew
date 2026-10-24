package com.github.saintleva.sourcechew.ui.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Instant
import kotlinx.datetime.TimeZone

class DateTimeFormatterTest : FunSpec({
    test("formatDateTime formats Instant correctly for given TimeZone") {
        val instant = Instant.parse("2024-03-15T14:30:00Z")
        val utcFormatted = instant.formatDateTime(TimeZone.UTC)
        utcFormatted shouldBe "2024-03-15 14:30"
    }
})
