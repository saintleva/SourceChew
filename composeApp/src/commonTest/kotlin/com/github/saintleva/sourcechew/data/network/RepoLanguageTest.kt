package com.github.saintleva.sourcechew.data.network

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

class RepoLanguageTest : FunSpec({

    context("toRepoLanguages mapping") {

        test("calculates percentages and sorts by byte count descending") {
            val languagesMap = mapOf(
                "Swift" to 2000L,
                "Kotlin" to 8000L,
                "Shell" to 1000L
            )

            val languages = languagesMap.toRepoLanguages()

            languages shouldHaveSize 3
            languages[0].name shouldBe "Kotlin"
            languages[0].bytes shouldBe 8000L
            languages[0].percentage shouldBe (72.72727f plusOrMinus 0.01f)

            languages[1].name shouldBe "Swift"
            languages[1].bytes shouldBe 2000L
            languages[1].percentage shouldBe (18.18181f plusOrMinus 0.01f)

            languages[2].name shouldBe "Shell"
            languages[2].bytes shouldBe 1000L
            languages[2].percentage shouldBe (9.09090f plusOrMinus 0.01f)
        }

        test("returns empty list for empty map or zero bytes") {
            emptyMap<String, Long>().toRepoLanguages() shouldBe emptyList()
            mapOf("Kotlin" to 0L).toRepoLanguages() shouldBe emptyList()
        }
    }
})
