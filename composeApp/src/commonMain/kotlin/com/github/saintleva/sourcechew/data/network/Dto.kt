package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.domain.repository.FoundItemsBlock
import com.github.saintleva.sourcechew.domain.result.DeserializationException
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

fun GithubSearchResponseDto<GithubOwnerDto>.toDomain() = FoundItemsBlock(
    items = items.map { it.toDomain() },
    metadata = SearchMetadata(totalCount, incompleteResults)
)

@Serializable
data class GithubRepoDto(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val url: String,
    val owner: GithubOwnerDto,
    val description: String?,
    val language: String?,
    @SerialName("stargazers_count") val stars: Int
)

// DTO for the owner of the repository
@Serializable
data class GithubOwnerDto(
    val id: Long,
    val type: String,
    val login: String,
    val url: String,
    val avatarUrl: String
)

fun GithubRepoDto.toDomain() = FoundRepo(
    id = id,
    name = name,
    fullName = fullName,
    url = url,
    owner = owner.toDomain(),
    description = description,
    language = language,
    stars = stars
)

fun GithubOwnerDto.toDomain() = FoundOwner(
    id = id,
    type = when (type) {
        "User" -> OwnerType.USER
        "Organization" -> OwnerType.ORGANIZATION
        else -> throw DeserializationException(IllegalStateException("Unknown owner type: $type"))
    },
    login = login,
    url = url,
    avatarUrl = avatarUrl
)

@Serializable
data class GithubErrorResponseDto(
    val message: String,
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

