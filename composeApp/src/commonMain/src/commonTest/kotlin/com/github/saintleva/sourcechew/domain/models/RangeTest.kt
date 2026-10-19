package com.github.saintleva.sourcechew.domain.models

import com.github.saintleva.sourcechew.data.network.utils.toGitHubQuery
import com.github.saintleva.sourcechew.data.network.utils.toGitHubValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RangeTest : FunSpec({

    test("Range exact creates exact range and formats correctly") {
        val range = Range.exact(10)
        range.isExact shouldBe true
        range.toGitHubValue() shouldBe "10"
        range.toGitHubQuery("repos") shouldBe "repos:10"
    }

    test("Range atLeast formats correctly") {
        val range = Range.atLeast(10)
        range.isExact shouldBe false
        range.toGitHubValue() shouldBe ">=10"
        range.toGitHubQuery("followers") shouldBe "followers:>=10"
    }

    test("Range atMost formats correctly") {
        val range = Range.atMost(50)
        range.isExact shouldBe false
        range.toGitHubValue() shouldBe "<=50"
        range.toGitHubQuery("stars") shouldBe "stars:<=50"
    }

    test("Range between formats correctly") {
        val range = Range.between(10, 50)
        range.isExact shouldBe false
        range.toGitHubValue() shouldBe "10..50"
        range.toGitHubQuery("repos") shouldBe "repos:10..50"
    }

    test("Range with string dates formats correctly") {
        val dateRange = Range.between("2023-01-01", "2023-12-31")
        dateRange.toGitHubValue() shouldBe "2023-01-01..2023-12-31"
        dateRange.toGitHubQuery("created") shouldBe "created:2023-01-01..2023-12-31"

        val exactDate = Range.exact("2024-05-20")
        exactDate.toGitHubValue() shouldBe "2024-05-20"
        exactDate.toGitHubQuery("created") shouldBe "created:2024-05-20"
    }
})
