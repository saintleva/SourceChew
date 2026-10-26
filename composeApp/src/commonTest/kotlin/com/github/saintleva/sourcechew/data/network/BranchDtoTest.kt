package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.RepoBranch
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class BranchDtoTest : FunSpec({

    val json = Json { ignoreUnknownKeys = true }

    test("deserializes GitHub branches JSON response and maps to domain RepoBranch") {
        val jsonPayload = """
            [
              {
                "name": "main",
                "commit": {
                  "sha": "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc",
                  "url": "https://api.github.com/repos/octocat/Hello-World/commits/c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"
                },
                "protected": true
              },
              {
                "name": "feature/branch-1",
                "commit": {
                  "sha": "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d",
                  "url": "https://api.github.com/repos/octocat/Hello-World/commits/7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"
                },
                "protected": false
              }
            ]
        """.trimIndent()

        val dtos = json.decodeFromString<List<GithubBranchDto>>(jsonPayload)
        dtos shouldHaveSize 2

        dtos[0].name shouldBe "main"
        dtos[0].commit.sha shouldBe "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"
        dtos[0].protected shouldBe true

        val domainBranches = dtos.map { it.toDomain() }
        domainBranches shouldBe listOf(
            RepoBranch(name = "main", commitSha = "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"),
            RepoBranch(name = "feature/branch-1", commitSha = "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"),
        )
    }
})
