package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.models.RepoDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

interface GithubDetailsDtoBase {
    val id: Long
    val url: String
    val htmlUrl: String
}

@Serializable
data class GithubLicenseDto(
    val key: String? = null,
    val name: String? = null,
    @SerialName("spdx_id") val spdxId: String? = null
)

@Serializable
data class GithubRepoDetailsDto(
    override val id: Long,
    val name: String,
    @SerialName("full_name") val fullname: String,
    override val url: String,
    @SerialName("html_url") override val htmlUrl: String,
    val owner: GithubOwnerDto,
    val description: String? = null,
    val language: String? = null,
    @SerialName("stargazers_count") val stars: Int = 0,
    @SerialName("forks_count") val forks: Int = 0,
    @SerialName("watchers_count") val watchers: Int = 0,
    @SerialName("open_issues_count") val openIssues: Int = 0,
    @SerialName("default_branch") val defaultBranch: String? = null,
    val license: GithubLicenseDto? = null,
    val topics: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("pushed_at") val pushedAt: String? = null,
    val homepage: String? = null,
    val archived: Boolean = false,
    val fork: Boolean = false
) : GithubDetailsDtoBase

@Serializable
data class GithubOwnerDetailsDto(
    override val id: Long,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    override val url: String,
    @SerialName("html_url") override val htmlUrl: String,
    val type: String,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null,
    @SerialName("public_repos") val publicRepos: Int = 0,
    @SerialName("public_gists") val publicGists: Int = 0,
    val followers: Int? = null,
    val following: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) : GithubDetailsDtoBase

private fun parseInstant(isoString: String?): Instant? =
    isoString?.let {
        try {
            Instant.parse(it)
        } catch (_: Exception) {
            null
        }
    }

fun GithubRepoDetailsDto.toDomain() = RepoDetails(
    id = id,
    name = name,
    fullName = fullname,
    url = url,
    htmlUrl = htmlUrl,
    owner = owner.toDomain(),
    description = description,
    language = language,
    stars = stars,
    forks = forks,
    watchers = watchers,
    openIssues = openIssues,
    defaultBranch = defaultBranch,
    licenseName = license?.name ?: license?.spdxId,
    topics = topics,
    createdAt = parseInstant(createdAt),
    updatedAt = parseInstant(updatedAt),
    pushedAt = parseInstant(pushedAt),
    homepageUrl = homepage?.takeIf { it.isNotBlank() },
    isArchived = archived,
    isFork = fork
)

fun GithubOwnerDetailsDto.toDomain() = OwnerDetailsBase(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
    url = url,
    htmlUrl = htmlUrl,
    type = when (type.lowercase()) {
        "user" -> OwnerType.USER
        "organization" -> OwnerType.ORGANIZATION
        else -> OwnerType.USER
    },
    name = name,
    company = company,
    blog = blog?.takeIf { it.isNotBlank() },
    location = location,
    email = email,
    bio = bio,
    publicRepos = publicRepos,
    publicGists = publicGists,
    followers = followers,
    following = following,
    createdAt = parseInstant(createdAt),
    updatedAt = parseInstant(updatedAt)
)
