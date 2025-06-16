/*
 * Copyright (C) Anton Liaukevich 2021-2022 <leva.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.saintleva.sourcechew.domain.models


private val githubUserRules = SearchRules(
    allowedMainParameter = Matching.contains,
    allowedParameters = mapOf(
        "in" to Matching.exact,
        "repos" to Matching.comparisons,
        "location" to Matching.exact,
        "created" to Matching.comparisons,
        "language" to Matching.exact
    )
)

sealed class Forge(
    val name: String,
    val searchRules: ForgeSearchRules
) {
    val supportUsers = searchRules.user != null
    val supportGroups = searchRules.group != null

    data object Github : Forge("GitHub",
        ForgeSearchRules(
            repo = SearchRules(
                allowedMainParameter = Matching.contains,
                allowedParameters = mapOf(
                    "in" to Matching.exact,
                    "size" to Matching.comparisons,
                    "forks" to Matching.comparisons,
                    "created" to Matching.comparisons,
                    "pushed" to Matching.comparisons,
                    "user" to Matching.exact,
                    "repo" to Matching.exact,
                    "language" to Matching.exact,
                    "stars" to Matching.comparisons,
                    "topic" to Matching.exact,
                    "license" to Matching.exact,
                    "archived" to Matching.exact,
                    "mirror" to Matching.exact,
                    "is" to Matching.exact,
                )
            ),
            user = githubUserRules,
            group = githubUserRules
        )
    )

    data object Gitlab : Forge("GitLab",
        ForgeSearchRules(
            repo = SearchRules(
                allowedMainParameter = Matching.contains,
                allowedParameters = mapOf(
                    "membership" to Matching.exact,
                    "owned" to Matching.exact,
                    "starred" to Matching.exact,
                    "visibility" to Matching.exact,
                    "archived" to Matching.exact,
                    "with_issues_enabled" to Matching.exact,
                    "with_merge_requests_enabled" to Matching.exact,
                    "min_access_level" to Matching.exact,
                    "statistics" to Matching.exact,
                    "language" to Matching.exact,
                    "topic" to Matching.exact,
                    "last_activity_after" to Matching.exact,
                    "last_activity_before" to Matching.exact,
                    "repository_checksum_failed" to Matching.exact
                )
            ),
            user = SearchRules(
                allowedMainParameter = setOf(Matching.Exact, Matching.Contains),
                allowedParameters = mapOf(
                    "external" to Matching.exact,
                    "created_before" to Matching.exact,
                    "created_after" to Matching.exact,
                    "active" to Matching.exact,
                    "blocked" to Matching.exact,
                    "external_uid" to Matching.exact,
                    "provider" to Matching.exact,
                    "without_projects" to Matching.exact
                )
            ),
            group = SearchRules(
                allowedMainParameter = Matching.contains,
                allowedParameters = mapOf(
                    "all_available" to Matching.exact,
                    "min_access_level" to Matching.exact,
                    "all" to Matching.exact,
                    "owned" to Matching.exact,
                    "starred" to Matching.exact,
                    "wiki_enabled" to Matching.exact,
                    "top_level_only" to Matching.exact,
                    "parent_id" to Matching.exact,
                    "user_id" to Matching.exact
                )
            )
        )
    )

    data object Bitbucket : Forge("Bitbucket",
        ForgeSearchRules(
            repo = SearchRules(
                allowedMainParameter = Matching.containing,
                allowedParameters = mapOf(
                    "full_name" to setOf(Matching.Contains, Matching.NotContains),
                    "owner.username" to Matching.containing,
                    "is_private" to Matching.exact,
                    "updated" to Matching.comparisons,
                    "created" to Matching.comparisons,
                    "language" to Matching.equaling,
                    "has_issues" to Matching.exact,
                    "slug" to Matching.containing
                )
            ),
            user = null,
            group = null
        )
    )

        //TODO: remove this
//    data object Sourceforge : Forge("SourceForge", false)
//    data object Gitea : Forge("Gitea", false)
//    data object Sourcehut : Forge("SourceHut", false)
//    data object Gogs : Forge("Gogs", false)
//    data object Codeberg : Forge("Codeberg", false)

    companion object {
        //TODO: remove this
        //val list = listOf(Github, Gitlab, Bitbucket, Sourceforge, Gitea, Sourcehut, Gogs, Codeberg)
        val list = listOf(Github, Gitlab, Bitbucket)
    }
}