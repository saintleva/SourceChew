package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.parseInstant
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.domain.repository.FoundItemsBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GithubSearchResponseDto<GithubItemDto>(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("incomplete_results") val incompleteResults: Boolean,
    val items: List<GithubItemDto>
)

fun <T, R : FoundBase> GithubSearchResponseDto<T>.toDomain(itemMapper: (T) -> R) = FoundItemsBlock(
    items = items.map { itemMapper(it) },
    metadata = SearchMetadata(totalCount, incompleteResults)
)

@Serializable
data class GithubRepoDto(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String? = null,
    val url: String? = null,
    val owner: GithubOwnerDto,
    val description: String? = null,
    val language: String? = null,
    @SerialName("stargazers_count") val stars: Int = 0,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("pushed_at") val pushedAt: String? = null,
)

// DTO for the owner of the repository
@Serializable
data class GithubOwnerDto(
    val id: Long,
    val type: String,
    val login: String,
    val url: String? = null,
    @SerialName("avatar_url") val avatarUrl: String
)

fun GithubRepoDto.toDomain() = FoundRepo(
    id = id,
    name = name,
    owner = owner.toDomain(),
    description = description,
    language = language,
    stars = stars,
    updatedAt = parseInstant(pushedAt ?: updatedAt)
)

fun GithubOwnerDto.toDomain() = FoundOwner(
    id = id,
    type = when (type.lowercase()) {
        "user" -> OwnerType.USER
        "organization" -> OwnerType.ORGANIZATION
        else -> throw IllegalArgumentException("Unknown owner type: $type")
    },
    login = login,
    avatarUrl = avatarUrl
)

@Serializable
data class GithubErrorResponseDto(
    val message: String? = null,
    val errors: List<GithubErrorDetailDto>? = null,
    @SerialName("documentation_url") val documentationUrl: String? = null
)

@Serializable
data class GithubErrorDetailDto(
    val resource: String? = null,
    val field: String? = null,
    val code: String? = null,
    val message: String? = null
)

