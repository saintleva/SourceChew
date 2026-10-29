package com.github.saintleva.sourcechew.ui.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.datetime.TimeZone

class DateTimeFormatterTest : FunSpec({
    test("formatDateTime formats Instant correctly for given TimeZone") {
        val instant = Instant.parse("2024-03-15T14:30:00Z")
        val utcFormatted = instant.formatDateTime(TimeZone.UTC)
        utcFormatted shouldBe "2024-03-15 14:30"
    }

    test("formatCompactNumber formats numbers correctly") {
        formatCompactNumber(0) shouldBe "0"
        formatCompactNumber(999) shouldBe "999"
        formatCompactNumber(1000) shouldBe "1k"
        formatCompactNumber(1700) shouldBe "1.7k"
        formatCompactNumber(12300) shouldBe "12.3k"
        formatCompactNumber(100000) shouldBe "100k"
        formatCompactNumber(1000000) shouldBe "1M"
        formatCompactNumber(2500000) shouldBe "2.5M"
    }

    test("toRelativeTimeText formats relative times correctly") {
        val now = Instant.parse("2024-03-15T12:00:00Z")

        (now - 10.seconds).toRelativeTimeText(now) shouldBe "just now"
        (now - 1.minutes).toRelativeTimeText(now) shouldBe "1 minute ago"
        (now - 15.minutes).toRelativeTimeText(now) shouldBe "15 minutes ago"
        (now - 1.hours).toRelativeTimeText(now) shouldBe "1 hour ago"
        (now - 5.hours).toRelativeTimeText(now) shouldBe "5 hours ago"
        (now - 1.days).toRelativeTimeText(now) shouldBe "1 day ago"
        (now - 10.days).toRelativeTimeText(now) shouldBe "10 days ago"
        (now - 60.days).toRelativeTimeText(now) shouldBe "2 months ago"
        (now - 400.days).toRelativeTimeText(now) shouldBe "1 year ago"
    }
})
