package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.License
import com.github.saintleva.sourcechew.domain.models.OrganizationDetails
import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.models.RepoLanguage
import com.github.saintleva.sourcechew.domain.models.UserDetails
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
    @SerialName("spdx_id") val spdxId: String? = null,
)

fun GithubLicenseDto.toDomain() = License(
    key = key,
    name = name,
    spdxId = spdxId,
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
    @SerialName("subscribers_count") val subscribersCount: Int = 0,
    @SerialName("open_issues_count") val openIssues: Int = 0,
    @SerialName("default_branch") val defaultBranch: String? = null,
    val license: GithubLicenseDto? = null,
    val topics: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("pushed_at") val pushedAt: String? = null,
    val homepage: String? = null,
    val archived: Boolean = false,
    val fork: Boolean = false,
) : GithubDetailsDtoBase

@Serializable
data class GithubOwnerDetailsDto(
    override val id: Long,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    override val url: String,
    @SerialName("html_url") override val htmlUrl: String,
    val type: String = "User",
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null,
    val description: String? = null,
    @SerialName("site_admin") val siteAdmin: Boolean = false,
    val hireable: Boolean? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("public_repos") val publicRepos: Int = 0,
    @SerialName("public_gists") val publicGists: Int = 0,
    val followers: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
) : GithubDetailsDtoBase

private fun parseInstant(isoString: String?): Instant? =
    isoString?.let {
        try {
            Instant.parse(it)
        } catch (_: Exception) {
            null
        }
    }

fun Map<String, Long>.toRepoLanguages(): List<RepoLanguage> {
    val totalBytes = values.sum()
    if (totalBytes <= 0L) return emptyList()
    return map { (name, bytes) ->
        val percentage = (bytes.toDouble() / totalBytes * 100.0).toFloat()
        RepoLanguage(
            name = name,
            bytes = bytes,
            percentage = percentage
        )
    }.sortedByDescending { it.bytes }
}

fun GithubRepoDetailsDto.toDomain(languages: List<RepoLanguage> = emptyList()) = RepoDetails(
    id = id,
    url = url,
    htmlUrl = htmlUrl,
    createdAt = parseInstant(createdAt),
    updatedAt = parseInstant(updatedAt),
    name = name,
    fullname = fullname,
    owner = owner.toDomain(),
    description = description,
    language = language,
    languages = languages,
    stars = stars,
    forks = forks,
    subscribersCount = subscribersCount,
    openIssues = openIssues,
    defaultBranch = defaultBranch,
    license = license?.toDomain(),
    topics = topics,
    pushedAt = parseInstant(pushedAt),
    homepageUrl = homepage?.takeIf { it.isNotBlank() },
    isArchived = archived,
    isFork = fork,
)

fun GithubOwnerDetailsDto.toDomain(): OwnerDetailsBase {
    return if (type.equals("organization", ignoreCase = true)) {
        OrganizationDetails(
            id = id,
            url = url,
            htmlUrl = htmlUrl,
            createdAt = parseInstant(createdAt),
            updatedAt = parseInstant(updatedAt),
            login = login,
            avatarUrl = avatarUrl,
            name = name,
            company = company,
            blog = blog?.takeIf { it.isNotBlank() },
            location = location,
            email = email,
            publicRepos = publicRepos,
            publicGists = publicGists,
            followers = followers,
            description = description ?: bio,
            isVerified = isVerified,
        )
    } else {
        UserDetails(
            id = id,
            url = url,
            htmlUrl = htmlUrl,
            createdAt = parseInstant(createdAt),
            updatedAt = parseInstant(updatedAt),
            login = login,
            avatarUrl = avatarUrl,
            name = name,
            company = company,
            blog = blog?.takeIf { it.isNotBlank() },
            location = location,
            email = email,
            publicRepos = publicRepos,
            publicGists = publicGists,
            followers = followers,
            bio = bio ?: description,
            hireable = hireable,
            siteAdmin = siteAdmin,
        )
    }
}
