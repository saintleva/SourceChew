package com.github.saintleva.sourcechew.data.remote

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GithubSearchResponseDto(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("incomplete_results") val incompleteResults: Boolean,
    val items: List<GithubRepoItemDto>
)

@Serializable
data class GithubRepoItemDto(
    val name: String,
    @SerialName("full_name") val fullName: String,
    val owner: GitHubOwnerDto,
    val description: String,
    val language: String,
    @SerialName("stargazers_count") val stars: Int
)

// DTO for the owner of the repository
@Serializable
data class GitHubOwnerDto(
    val login: String,
    val type: String
)

fun GithubRepoItemDto.toDomain() = FoundRepo(
    name = name,
    fullName = fullName,
    ownerLogin = owner.login,
    ownerType = owner.type,
    description = description,
    language = language,
    stars = stars
)


fun GithubSearchResponseDto.toDomain() = items.map { it.toDomain() }


class KtorRestApiService(
    private val httpClient: HttpClient,
    private val searchDispatcher: CoroutineDispatcher = Dispatchers.IO
): SearchApiService {

    companion object {
        const val BASE_URL = "https://api.github.com/search/repositories"

        val sortVariants = mapOf(
            RepoSearchSort.BEST_MATCH to "best match", //TODO: Is it right?
            RepoSearchSort.STARS to "stars",
            RepoSearchSort.FORKS to "forks",
            RepoSearchSort.UPDATED to "updated"
        )

        val orderVariants = mapOf(
            SearchOrder.ASCENDING to "asc",
            SearchOrder.DESCENDING to "desc"
        )
    }

    override suspend fun searchItems(
        conditions: RepoSearchConditions,
        page: Int,
        pageSize: Int
    ): List<FoundRepo> {
        val dto: GithubSearchResponseDto = httpClient.get(BASE_URL) {
            parameter("q", conditions.query)
            parameter("sort", sortVariants[conditions.sort]!!)
            parameter("order", orderVariants[conditions.order]!!)
            parameter("page", page)
            parameter("per_page", pageSize)
        }.body()
        return dto.toDomain()
    }
}