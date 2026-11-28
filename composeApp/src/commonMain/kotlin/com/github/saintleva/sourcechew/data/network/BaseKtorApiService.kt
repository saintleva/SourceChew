package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.isNetworkException
import com.github.saintleva.sourcechew.domain.result.AppError
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.CommonApiError
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.Result
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
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

interface BaseKtorApiService<Request, out Answer, out Error : AppError> {

    // Performs the actual HTTP request
    suspend fun getHttpResponse(request: Request): HttpResponse

    // Delegated parsing of successful response to the specific subclass
    suspend fun deserializeSuccess(response: HttpResponse): Answer

    // Delegated mapping of non-successful response to the specific error type
    suspend fun mapErrorResponse(response: HttpResponse): Error

    /**
     * Maps standard HTTP error status codes (4xx, 5xx) to [CommonApiError].
     * Takes into account standard GitHub Rate Limit headers (x-ratelimit-remaining, retry-after).
     */
    suspend fun mapCommonApiError(response: HttpResponse): CommonApiError =
        Companion.mapCommonApiError(response)

    /**
     * Parses error message from response body.
     */
    suspend fun parseErrorMessage(response: HttpResponse, defaultMessage: String): String =
        Companion.parseErrorMessage(response, defaultMessage)

    suspend fun getAnswer(request: Request): Result<Answer, Error> {
        val logTag = this::class.simpleName ?: "BaseKtorApiService"
        return executeSafeApiCall(logTag) {
            val response = getHttpResponse(request)

            when {
                // Handle successful responses
                response.status.isSuccess() -> {
                    try {
                        Result.Success(deserializeSuccess(response))
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: AppException) {
                        throw e
                    } catch (e: Exception) {
                        throw DeserializationException(e)
                    }
                }

                // Handle expected API errors and map them to DomainError
                else -> {
                    Result.Failure(mapErrorResponse(response))
                }
            }
        }
    }

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        /**
         * Parses error message from response body.
         */
        suspend fun parseErrorMessage(response: HttpResponse, defaultMessage: String): String {
            val rawBody = response.bodyAsText()
            return try {
                val errorDto = json.decodeFromString<GithubErrorResponseDto>(rawBody)
                val details = errorDto.errors?.joinToString("; ") { detail ->
                    detail.message
                        ?: "Error in field '${detail.field}' (code: ${detail.code})"
                }
                val message = errorDto.message
                if (!details.isNullOrBlank()) {
                    if (message != null) "$message: $details" else details
                } else {
                    message ?: defaultMessage
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                rawBody.takeIf { it.isNotBlank() } ?: defaultMessage
            }
        }

        /**
         * Parses common GitHub API errors from [HttpResponse], checking status codes and
         * rate limiting headers (`x-ratelimit-remaining`, `retry-after`).
         */
        suspend fun mapCommonApiError(response: HttpResponse): CommonApiError {
            val status = response.status

            val hasRateLimitExceededHeader = response.headers["x-ratelimit-remaining"]?.trim() == "0" ||
                    response.headers.contains("retry-after")

            return when {
                status == HttpStatusCode.Unauthorized -> {
                    CommonApiError.Unauthorized
                }
                status == HttpStatusCode.TooManyRequests -> {
                    CommonApiError.RateLimitExceeded
                }
                status == HttpStatusCode.Forbidden -> {
                    if (hasRateLimitExceededHeader) {
                        CommonApiError.RateLimitExceeded
                    } else {
                        CommonApiError.CommonAccessError
                    }
                }
                status == HttpStatusCode.BadRequest -> {
                    CommonApiError.BadRequest(parseErrorMessage(response, "Bad Request (400)"))
                }
                status == HttpStatusCode.Conflict -> {
                    CommonApiError.Conflict(parseErrorMessage(response, "Conflict (409)"))
                }
                status == HttpStatusCode.UnprocessableEntity -> {
                    CommonApiError.UnprocessableEntity(parseErrorMessage(response, "Unprocessable Entity (422)"))
                }
                status.value in 500..599 -> {
                    CommonApiError.ServerError
                }
                else -> {
                    val message = parseErrorMessage(response, "Unknown error (${status.value})")
                    Napier.e(tag = "BaseKtorApiService") {
                        "Unknown error: ${status.value}, body: $message"
                    }
                    CommonApiError.UnknownApiError(status.value, message)
                }
            }
        }
    }
}

/**
 * Executes a network call and translates low-level network/infrastructure exceptions into domain exceptions
 * ([NetworkException], [UnknownInfrastructureException]), while preserving coroutine cancellation
 * and domain [AppException]s.
 */
suspend inline fun <T> executeSafeApiCall(
    logTag: String,
    crossinline block: suspend () -> T
): T {
    try {
        return block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: AppException) {
        throw e
    } catch (e: Exception) {
        // This block catches exceptions that occur *before* we can inspect the HTTP response.
        // This includes network issues (no internet), DNS problems, timeouts, etc.
        val domainException = when {
            isNetworkException(e) -> NetworkException(e)
            e is ServerResponseException || e is RedirectResponseException ||
                    e is ClientRequestException -> UnknownInfrastructureException(e)
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
        throw domainException
    }
}
