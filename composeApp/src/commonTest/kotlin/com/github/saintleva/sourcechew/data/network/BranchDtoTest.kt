package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.Commit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.time.Instant
import kotlinx.serialization.json.Json

class BranchDtoTest : FunSpec({

    val json = Json { ignoreUnknownKeys = true }

    test("deserializes GitHub branches JSON response and maps to domain Branch") {
        val jsonPayload = """
            [
              {
                "name": "main",
                "commit": {
                  "sha": "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc",
                  "commit": {
                    "committer": {
                      "date": "2024-03-15T14:30:00Z"
                    }
                  }
                }
              },
              {
                "name": "feature/branch-1",
                "commit": {
                  "sha": "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"
                }
              }
            ]
        """.trimIndent()

        val dtos = json.decodeFromString<List<GithubBranchDto>>(jsonPayload)
        dtos shouldHaveSize 2

        dtos[0].name shouldBe "main"
        dtos[0].commit.sha shouldBe "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"

        val domainBranches = dtos.map { it.toDomain() }
        domainBranches shouldBe listOf(
            Branch(
                name = "main",
                commit = Commit(
                    sha = "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc",
                    madeAt = Instant.parse("2024-03-15T14:30:00Z")
                )
            ),
            Branch(
                name = "feature/branch-1",
                commit = Commit(
                    sha = "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d",
                    madeAt = null
                )
            ),
        )
    }
})
