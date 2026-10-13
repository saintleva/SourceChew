package com.github.saintleva.sourcechew.domain.models

import com.github.saintleva.sourcechew.data.network.utils.toGitHubQuery
import com.github.saintleva.sourcechew.data.network.utils.toGitHubValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

class RangeTest : FunSpec({

    context("Int ranges") {

        test("exact creates exact range and formats correctly") {
            val range = Range.exact(10)
            range.isExact shouldBe true
            range.toGitHubValue() shouldBe "10"
            range.toGitHubQuery("repos") shouldBe "repos:10"
        }

        test("atLeast formats correctly") {
            val range = Range.atLeast(10)
            range.isExact shouldBe false
            range.toGitHubValue() shouldBe ">=10"
            range.toGitHubQuery("followers") shouldBe "followers:>=10"
        }

        test("atMost formats correctly") {
            val range = Range.atMost(50)
            range.isExact shouldBe false
            range.toGitHubValue() shouldBe "<=50"
            range.toGitHubQuery("stars") shouldBe "stars:<=50"
        }

        test("between formats correctly") {
            val range = Range.between(10, 50)
            range.isExact shouldBe false
            range.toGitHubValue() shouldBe "10..50"
            range.toGitHubQuery("repos") shouldBe "repos:10..50"
        }
    }

    context("LocalDate ranges") {

        test("between formats date range correctly") {
            val dateRange = Range.between(
                LocalDate(2023, 1, 1),
                LocalDate(2023, 12, 31),
            )
            dateRange.toGitHubValue() shouldBe "2023-01-01..2023-12-31"
            dateRange.toGitHubQuery("created") shouldBe "created:2023-01-01..2023-12-31"
        }

        test("exact date formats correctly") {
            val exactDate = Range.exact(LocalDate(2024, 5, 20))
            exactDate.toGitHubValue() shouldBe "2024-05-20"
            exactDate.toGitHubQuery("created") shouldBe "created:2024-05-20"
        }
    }

    context("Instant ranges") {

        test("between formats ISO 8601 instant range correctly") {
            val startInstant = Instant.parse("2016-03-21T14:11:00Z")
            val endInstant = Instant.parse("2016-04-07T20:45:00Z")
            val instantRange = Range.between(startInstant, endInstant)

            instantRange.toGitHubValue() shouldBe "2016-03-21T14:11:00Z..2016-04-07T20:45:00Z"
            instantRange.toGitHubQuery("created") shouldBe "created:2016-03-21T14:11:00Z..2016-04-07T20:45:00Z"
        }

        test("exact instant formats correctly") {
            val instant = Instant.parse("2016-03-21T14:11:00Z")
            val exactInstant = Range.exact(instant)

            exactInstant.toGitHubValue() shouldBe "2016-03-21T14:11:00Z"
            exactInstant.toGitHubQuery("created") shouldBe "created:2016-03-21T14:11:00Z"
        }
    }
})
