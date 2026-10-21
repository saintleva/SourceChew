package com.github.saintleva.sourcechew.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    val fullName: String,
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


data class OwnerDetailsBase(
    override val id: Long,
    override val url: String,
    override val htmlUrl: String,
    override val createdAt: Instant?,
    override val updatedAt: Instant?,
    val login: String,
    val avatarUrl: String,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val publicRepos: Int,
    val publicGists: Int,
    val followers: Int,
) : DetailsBase

data class UserDetails(
    val bio: String?,
    val hireable: Boolean?,
    val siteAdmin: Boolean
) : OwnerDetailsBase

data class OrganizationDetails(
    val description: String?,
    val isVerified: Boolean
) : OwnerDetailsBase