package com.github.saintleva.sourcechew.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainKeys
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class SearchRulesTest : FunSpec({

    context("Forge.Github.searchRules") {
        val forge = Forge.Github

        test("repo rules should be configured correctly") {
            val repoRules = forge.searchRules.repo
            repoRules.shouldNotBeNull()
            repoRules.allowedMainParameter shouldBe Matching.contains
            repoRules.allowedParameters.shouldContainKeys(
                "in", "size", "forks", "created", "pushed", "user", "repo",
                "language", "stars", "topic", "license", "archived", "mirror", "is"
            )
            repoRules.allowedParameters["in"] shouldBe Matching.exact
            repoRules.allowedParameters["size"] shouldBe Matching.comparisons
            // Добавьте больше проверок для других специфичных параметров, если нужно
        }

        test("user rules should be configured correctly") {
            val userRules = forge.searchRules.user
            userRules.shouldNotBeNull()
            // Эти правила ссылаются на githubUserRules
            userRules.allowedMainParameter shouldBe Matching.contains
            userRules.allowedParameters.shouldContainKeys(
                "in", "repos", "location", "created", "language"
            )
            userRules.allowedParameters["repos"] shouldBe Matching.comparisons
        }

        test("group rules should be configured correctly (same as user rules)") {
            val groupRules = forge.searchRules.group
            groupRules.shouldNotBeNull()
            // Эти правила также ссылаются на githubUserRules
            groupRules.allowedMainParameter shouldBe Matching.contains
            groupRules.allowedParameters.shouldContainKeys(
                "in", "repos", "location", "created", "language"
            )
        }

        test("supportUsers should be true") {
            forge.supportUsers shouldBe true
        }

        test("supportGroups should be true") {
            forge.supportGroups shouldBe true
        }
    }

    context("Forge.Gitlab.searchRules") {
        val forge = Forge.Gitlab

        test("repo rules should be configured correctly") {
            val repoRules = forge.searchRules.repo
            repoRules.shouldNotBeNull()
            repoRules.allowedMainParameter shouldBe Matching.contains
            repoRules.allowedParameters.shouldContainKeys(
                "membership", "owned", "starred", "visibility", "archived",
                "with_issues_enabled", "with_merge_requests_enabled",
                "min_access_level", "statistics", "language", "topic",
                "last_activity_after", "last_activity_before", "repository_checksum_failed"
            )
            repoRules.allowedParameters["membership"] shouldBe Matching.exact
        }

        test("user rules should be configured correctly") {
            val userRules = forge.searchRules.user
            userRules.shouldNotBeNull()
            userRules.allowedMainParameter shouldBe setOf(Matching.Exact, Matching.Contains)
            userRules.allowedParameters.shouldContainKeys(
                "external", "created_before", "created_after", "active",
                "blocked", "external_uid", "provider", "without_projects"
            )
            userRules.allowedParameters["external"] shouldBe Matching.exact
        }

        test("group rules should be configured correctly") {
            val groupRules = forge.searchRules.group
            groupRules.shouldNotBeNull()
            groupRules.allowedMainParameter shouldBe Matching.contains
            groupRules.allowedParameters.shouldContainKeys(
                "all_available", "min_access_level", "all", "owned",
                "starred", "wiki_enabled", "top_level_only", "parent_id", "user_id"
            )
        }

        test("supportUsers should be true") {
            forge.supportUsers shouldBe true
        }

        test("supportGroups should be true") {
            forge.supportGroups shouldBe true
        }
    }

    context("Forge.Bitbucket.searchRules") {
        val forge = Forge.Bitbucket

        test("repo rules should be configured correctly") {
            val repoRules = forge.searchRules.repo
            repoRules.shouldNotBeNull()
            repoRules.allowedMainParameter shouldBe Matching.containing
            repoRules.allowedParameters.shouldContainKeys(
                "full_name", "owner.username", "is_private", "updated",
                "created", "language", "has_issues", "slug"
            )
            repoRules.allowedParameters["full_name"] shouldBe setOf(Matching.Contains, Matching.NotContains)
            repoRules.allowedParameters["language"] shouldBe Matching.equaling
        }

        test("user rules should be null") {
            forge.searchRules.user should beNull()
        }

        test("group rules should be null") {
            forge.searchRules.group should beNull()
        }

        test("supportUsers should be false") {
            forge.supportUsers shouldBe false
        }

        test("supportGroups should be false") {
            forge.supportGroups shouldBe false
        }
    }
})