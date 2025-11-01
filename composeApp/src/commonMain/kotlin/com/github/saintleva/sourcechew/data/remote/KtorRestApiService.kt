package com.github.saintleva.sourcechew.data.remote

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.SearchError
import com.github.saintleva.sourcechew.domain.result.SearchResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
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
    private val httpClient: HttpClient
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
    ): SearchResult<List<FoundRepo>> {
        val response = httpClient.get {
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
            // Important: Disable the default exception throwing for 4xx and 5xx statuses
            // so we can handle them manually.
            expectSuccess = false
        }

        return when {
            // Handle successful responses
            response.status.isSuccess() -> {
                try {
                    // On success, parse the body and wrap it in Result.success
                    SearchResult<List<FoundRepo>>.Success(response.body<GithubSearchResponseDto>().toDomain())
                } catch (e: Exception) {
                    // A parsing failure on a successful response is an infrastructure error.
                    throw DeserializationException(e)
                }
            }

            // Handle expected API errors and map them to DomainError
            response.status == HttpStatusCode.UnprocessableEntity -> { // 422
                SearchResult.Failure(SearchError.Validation(response.bodyAsText()))
            }

            response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden -> { // 401, 403
                SearchResult.Failure(SearchError.ApiLimitOrAuth)
            }

            response.status == HttpStatusCode.NotFound -> { // 404
                SearchResult.Failure(SearchError.NotFound)
            }

            response.status.value in 500..599 -> {
                SearchResult.Failure(SearchError.ServerError)
            }

            else -> {
                // Handle any other non-successful status codes
                SearchResult.Failure(SearchError.UnknownApiError(response.status.value))
            }
        }
    }
}