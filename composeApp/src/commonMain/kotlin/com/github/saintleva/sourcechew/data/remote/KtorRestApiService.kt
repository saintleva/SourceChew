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
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val owner: GitHubOwnerDto,
    val description: String?,
    val language: String?,
    @SerialName("stargazers_count") val stars: Int
)

// DTO for the owner of the repository
@Serializable
data class GitHubOwnerDto(
    val login: String,
    val type: String
)

fun GithubRepoItemDto.toDomain() = FoundRepo(
    id = id,
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
        const val API_BASE_URL = "api.github.com"
        const val SEARCH_REPOSITORIES_ENDPOINT = "/search/repositories"

        val sortVariants = mapOf(
            RepoSearchSort.BEST_MATCH to null,
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
        val response: HttpResponse
        withContext(searchDispatcher) {
            response = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = API_BASE_URL
                    path(SEARCH_REPOSITORIES_ENDPOINT)
                    parameter("q", conditions.query)
                    sortVariants[conditions.sort]?.let { parameter("sort", it) }
                    parameter("order", orderVariants[conditions.order]!!)
                    parameter("page", page.toString())
                    parameter("per_page", pageSize.toString())
                }
            }
        }
        if (response.status.isSuccess()) {
        return response.body<GithubSearchResponseDto>().toDomain()
    }
}