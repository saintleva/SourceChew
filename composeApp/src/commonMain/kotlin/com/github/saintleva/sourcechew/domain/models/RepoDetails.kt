package com.github.saintleva.sourcechew.domain.models

import kotlin.time.Instant

data class RepoDetails(
    override val id: Long,
    override val url: String,
    override val htmlUrl: String,
    override val createdAt: Instant?,
    override val updatedAt: Instant?,
    val name: String,
    val fullName: String,
    val owner: FoundOwner,
    val description: String?,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val openIssues: Int,
    val defaultBranch: String?,
    val licenseName: String?,
    val topics: List<String>,
    val pushedAt: Instant?,
    val homepageUrl: String?,
    val isArchived: Boolean,
    val isFork: Boolean
) : DetailsBase
