package com.github.saintleva.sourcechew.domain.models

import kotlin.time.Instant


sealed interface DetailsBase {
    val id: Long
    val url: String
    val htmlUrl: String
    val createdAt: Instant?
    val updatedAt: Instant?
}

data class License(
    val key: String? = null,
    val name: String? = null,
    val spdxId: String? = null
)

data class RepoDetails(
    override val id: Long,
    override val url: String,
    override val htmlUrl: String,
    override val createdAt: Instant?,
    override val updatedAt: Instant?,
    val name: String,
    val fullname: String,
    val owner: FoundOwner,
    val description: String?,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val subscribersCount: Int,
    val openIssues: Int,
    val defaultBranch: String?,
    val license: License?,
    val topics: List<String>,
    val pushedAt: Instant?,
    val homepageUrl: String?,
    val isArchived: Boolean,
    val isFork: Boolean
) : DetailsBase

interface OwnerDetailsBase : DetailsBase {
    val login: String
    val avatarUrl: String
    val name: String?
    val company: String?
    val blog: String?
    val location: String?
    val email: String?
    val publicRepos: Int
    val publicGists: Int
    val followers: Int
}

data class UserDetails(
    override val id: Long,
    override val url: String,
    override val htmlUrl: String,
    override val createdAt: Instant?,
    override val updatedAt: Instant?,
    override val login: String,
    override val avatarUrl: String,
    override val name: String?,
    override val company: String?,
    override val blog: String?,
    override val location: String?,
    override val email: String?,
    override val publicRepos: Int,
    override val publicGists: Int,
    override val followers: Int,
    val bio: String?,
    val hireable: Boolean?,
    val siteAdmin: Boolean
) : OwnerDetailsBase

data class OrganizationDetails(
    override val id: Long,
    override val url: String,
    override val htmlUrl: String,
    override val createdAt: Instant?,
    override val updatedAt: Instant?,
    override val login: String,
    override val avatarUrl: String,
    override val name: String?,
    override val company: String?,
    override val blog: String?,
    override val location: String?,
    override val email: String?,
    override val publicRepos: Int,
    override val publicGists: Int,
    override val followers: Int,
    val description: String?,
    val isVerified: Boolean
) : OwnerDetailsBase