package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.isNetworkException
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.repository.FoundItemsBlock
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.result.SearchError
import com.github.saintleva.sourcechew.domain.result.SearchResult
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlin.coroutines.cancellation.CancellationException


interface BaseKtorRestApiService<ItemSearchConditions, out FoundItem: FoundBase> {

    // Performs the actual HTTP reques
    suspend fun getHttpResponse(
        conditions: ItemSearchConditions,
        page: Int,
        pageSize: Int
    ): HttpResponse

    // Delegated parsing of successful response to the specific subclass
    suspend fun deserializeSuccess(response: HttpResponse): FoundItemsBlock<FoundItem>

    suspend fun searchItems(
        conditions: ItemSearchConditions,
        page: Int,
        pageSize: Int
    ): SearchResult<FoundItemsBlock<FoundItem>> {
        val logTag = this::class.simpleName ?: "BaseKtorRestApiService"
        try {
            val response = getHttpResponse(conditions, page, pageSize)

            return when {
                // Handle successful responses
                response.status.isSuccess() -> {
                    try {
                        // Delegate parsing to the subclass and wrap it in our custom Result.Success
                        Result.Success(deserializeSuccess(response))
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        if (e is AppException) throw e
                        // A parsing failure on a successful response is an infrastructure error
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
                    Napier.e(tag = "KtorRepoRestApiService") {
                        "Unknown error: ${response.status.value}, body: $unknownBody"
                    }
                    Result.Failure(SearchError.UnknownApiError(response.status.value))
                }
            }
        } catch (e: Exception) {
            // This block catches exceptions that occur *before* we can inspect the HTTP response.
            // This includes network issues (no internet), DNS problems, timeouts, etc.
            if (e is CancellationException) throw e
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
                    Napier.d(tag = logTag) {
                        "Caught unknown exception class: ${e::class.simpleName}, message: ${e.message}"
                    }
                    UnknownInfrastructureException(e)
                }
            }

            Napier.d(tag = logTag, throwable = domainException) {
                "before 'throw domainException'"
            }
            // Propagate our domain-specific exception to be handled by the caller (the Paginator load lambda).
            throw domainException
        }
    }
}