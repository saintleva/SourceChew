package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.isNetworkException
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
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
import kotlin.jvm.JvmName


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

fun GithubSearchResponseDto.toDomain() = items.map { it.toDomain() }


class KtorRestApiService(
    private val httpClient: HttpClient,
): SearchApiService {

    companion object {
        const val SEARCH_REPOSITORIES_ENDPOINT = "/search/repositories"

        private fun RepoSearchScope.toApiValue(): String = when (this) {
            RepoSearchScope.NAME -> "name"
            RepoSearchScope.DESCRIPTION -> "description"
            RepoSearchScope.README -> "readme"
        }

        @JvmName("toApiValueScope")
        private fun Set<RepoSearchScope>.toApiValue(): String =
            joinToString(",") { it.toApiValue() }

        private fun OnlyFlag.toApiValue(): String = when (this) {
            OnlyFlag.PUBLIC -> "public"
            OnlyFlag.PRIVATE -> "private"
            OnlyFlag.FORK -> "fork"
            OnlyFlag.ARCHIVED -> "archived"
            OnlyFlag.MIRROR -> "mirror"
            OnlyFlag.TEMPLATE -> "template"
        }

        @JvmName("toApiValueFlags")
        private fun Set<OnlyFlag>.toApiValue(): String =
            joinToString(" ") { "is:${it.toApiValue()}" }

        private fun RepoSearchSort.toApiValue(): String? = when (this) {
            RepoSearchSort.BEST_MATCH -> null
            RepoSearchSort.STARS -> "stars"
            RepoSearchSort.FORKS -> "forks"
            RepoSearchSort.UPDATED -> "updated"
        }

        private fun SearchOrder.toApiValue(): String = when (this) {
            SearchOrder.ASCENDING -> "asc"
            SearchOrder.DESCENDING -> "desc"
        }
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

                    val queryValue = buildString {
                        append(conditions.query)
                        if (conditions.inScope.isNotEmpty()) {
                            append(" in:${conditions.inScope.toApiValue()}")
                        }
                        if (conditions.onlyFlags.isNotEmpty()) {
                            append(" ${conditions.onlyFlags.toApiValue()}")
                        }
                    }
                    Napier.d(tag = "KtorRestApiService") { "Query: \"$queryValue\"" }

                    parameter("q", queryValue)
                    conditions.sort.toApiValue()?.let { parameter("sort", it) }
                    parameter("order", conditions.order.toApiValue())
                    parameter("page", page)
                    parameter("per_page", pageSize)
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