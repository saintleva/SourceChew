package com.github.saintleva.sourcechew.domain.models

import com.github.saintleva.sourcechew.data.network.utils.toGitHubQuery
import com.github.saintleva.sourcechew.data.network.utils.toGitHubValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

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
})
