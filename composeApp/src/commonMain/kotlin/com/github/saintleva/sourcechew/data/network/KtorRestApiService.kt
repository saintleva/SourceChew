package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.isNetworkException
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.RepoOnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.domain.repository.FoundItemsBlock
import com.github.saintleva.sourcechew.domain.repository.FoundReposBlock
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.result.AppException
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


class KtorRestApiService<SearchConditions, out FoundItem: FoundBase>(
    private val httpClient: HttpClient,
): SearchApiService<SearchConditions, FoundItem> {

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

        private fun RepoOnlyFlag.toApiValue(): String = when (this) {
            RepoOnlyFlag.PUBLIC -> "public"
            RepoOnlyFlag.PRIVATE -> "private"
            RepoOnlyFlag.FORK -> "fork"
            RepoOnlyFlag.ARCHIVED -> "archived"
            RepoOnlyFlag.MIRROR -> "mirror"
            RepoOnlyFlag.TEMPLATE -> "template"
        }

        @JvmName("toApiValueFlags")
        private fun Set<RepoOnlyFlag>.toApiValue(): String =
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
        conditions: SearchConditions,
        page: Int,
        pageSize: Int
    ): SearchResult<FoundItemsBlock<FoundItem>> {
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
                    val rawBody = response.bodyAsText()
                    val errorMessage = try {
                        // Using the same JSON configuration as the client
                        val errorDto = response.body<GithubErrorResponseDto>()

                        // Formatting detailed error messages from GitHub's list
                        val details = errorDto.errors?.joinToString("; ") { detail ->
                            detail.message
                                ?: "Error in field '${detail.field}' (code: ${detail.code})"
                        }

                        if (!details.isNullOrBlank()) {
                            "${errorDto.message}: $details"
                        } else {
                            errorDto.message
                        }
                    } catch (e: Exception) {
                        // Fallback if JSON parsing fails or body is not a valid GithubErrorResponseDto
                        rawBody.takeIf { it.isNotBlank() } ?: "Validation Error (422)"
                    }
                    Result.Failure(SearchError.Validation(errorMessage))
                }

                response.status == HttpStatusCode.Unauthorized -> { // 401
                    Result.Failure(SearchError.Unauthorized)
                }

                response.status == HttpStatusCode.Forbidden -> { // 403
                    val body = response.bodyAsText()
                    if (body.contains("rate limit", ignoreCase = true)) {
                        Result.Failure(SearchError.RateLimitExceeded)
                    } else {
                        Result.Failure(SearchError.CommonAccessError)
                    }
                }

                response.status == HttpStatusCode.NotFound -> { // 404
                    Result.Failure(SearchError.NotFound)
                }

                response.status.value in 500..599 -> {
                    Result.Failure(SearchError.ServerError)
                }

                else -> {
                    // Handle any other non-successful status codes
                    val unknownBody = response.bodyAsText()
                    Napier.e(tag = "KtorRestApiService") {
                        "Unknown error: ${response.status.value}, body: $unknownBody"
                    }
                    Result.Failure(SearchError.UnknownApiError(response.status.value))
                }
            }
        } catch (e: Exception) {
            // This block catches exceptions that occur *before* we can inspect the HTTP response.
            // This includes network issues (no internet), DNS problems, timeouts, etc.
            if (e is AppException) throw e

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

            Napier.d(tag = "KtorRestApiService", throwable = domainException) {
                "before 'throw domainException'"
            }
            // Propagate our domain-specific exception to be handled by the caller (the Paginator load lambda).
            throw domainException
        }
    }
}