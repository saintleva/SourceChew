package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.isNetworkException
import com.github.saintleva.sourcechew.domain.result.AppError
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.CommonApiError
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlin.coroutines.cancellation.CancellationException

interface BaseKtorApiService<Request, out Answer, out Error : AppError> {

    // Performs the actual HTTP request
    suspend fun getHttpResponse(request: Request): HttpResponse

    // Delegated parsing of successful response to the specific subclass
    suspend fun deserializeSuccess(response: HttpResponse): Answer

    // Delegated mapping of non-successful response to the specific error type
    suspend fun mapErrorResponse(response: HttpResponse): Error

    /**
     * Maps standard HTTP status codes (401, 403, 429, 5xx) to [CommonApiError].
     */
    suspend fun mapCommonApiError(response: HttpResponse): CommonApiError {
        return when {
            response.status == HttpStatusCode.Unauthorized -> {
                CommonApiError.Unauthorized
            }
            response.status == HttpStatusCode.Forbidden -> {
                val body = response.bodyAsText()
                if (body.contains("rate limit", ignoreCase = true)) {
                    CommonApiError.RateLimitExceeded
                } else {
                    CommonApiError.CommonAccessError
                }
            }
            response.status == HttpStatusCode.TooManyRequests -> {
                CommonApiError.RateLimitExceeded
            }
            response.status.value in 500..599 -> {
                CommonApiError.ServerError
            }
            else -> {
                val unknownBody = response.bodyAsText()
                Napier.e(tag = "BaseKtorApiService") {
                    "Unknown error: ${response.status.value}, body: $unknownBody"
                }
                CommonApiError.UnknownApiError(response.status.value)
            }
        }
    }

    suspend fun getAnswer(request: Request): Result<Answer, Error> {
        val logTag = this::class.simpleName ?: "BaseKtorApiService"
        try {
            val response = getHttpResponse(request)

            return when {
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
}
