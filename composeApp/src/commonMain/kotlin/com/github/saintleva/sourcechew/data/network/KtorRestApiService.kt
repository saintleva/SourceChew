package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.isNetworkException
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.result.SearchError
import com.github.saintleva.sourcechew.domain.result.SearchResult
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
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
    private val httpClient: HttpClient,
): SearchApiService {

    companion object {
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
        try {
            val response = httpClient.get {
                url {
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
                        Result.Success(response.body<GithubSearchResponseDto>().toDomain())
                    } catch (e: Exception) {
                        // A parsing failure on a successful response is an infrastructure error.
                        throw DeserializationException(e)
                    }
                }

                // Handle expected API errors and map them to DomainError
                response.status == HttpStatusCode.UnprocessableEntity -> { // 422
                    Result.Failure(SearchError.Validation(response.bodyAsText()))
                }

                response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden -> { // 401, 403
                    Result.Failure(SearchError.ApiLimitOrAuth)
                }

                response.status == HttpStatusCode.NotFound -> { // 404
                    Result.Failure(SearchError.NotFound)
                }

                response.status.value in 500..599 -> {
                    Result.Failure(SearchError.ServerError)
                }

                else -> {
                    // Handle any other non-successful status codes
                    Result.Failure(SearchError.UnknownApiError(response.status.value))
                }
            }
        } catch (e: Exception) {
            // This block catches exceptions that occur *before* we can inspect the HTTP response.
            // This includes network issues (no internet), DNS problems, timeouts, etc.
            val domainException = when {
                // Ktor often throws IOException for connectivity problems (e.g., Airplane Mode).
                isNetworkException(e) -> NetworkException(e)

                // These are Ktor's specific exceptions for HTTP-level failures.
                // We catch them here as a fallback, although `expectSuccess = false` should prevent most of them.
                e is ServerResponseException || e is RedirectResponseException ||
                        e is ClientRequestException -> UnknownInfrastructureException(e)

                // For any other unexpected exception, wrap it as an unknown infrastructure error.
                else -> {
                    Napier.d(tag = "KtorRestApiService") {
                        "Caught unknown exception class: ${e::class.simpleName}, message: ${e.message}"
                    }
                    UnknownInfrastructureException(e)
                }
            }
            // Propagate our domain-specific exception to be handled by the caller (e.g., PagingSource).
            throw domainException
        }
    }
}