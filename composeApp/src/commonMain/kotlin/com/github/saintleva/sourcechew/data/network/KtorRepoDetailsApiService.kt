package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.isNetworkException
import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.result.ApiCallException
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.result.SearchError
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlin.coroutines.cancellation.CancellationException

interface RepoDetailsApiService {
    suspend fun getRepoDetails(owner: String, name: String): Result<RepoDetails, AppException>
}

class KtorRepoDetailsApiService(
    private val httpClient: HttpClient
) : RepoDetailsApiService {

    override suspend fun getRepoDetails(owner: String, name: String): Result<RepoDetails, AppException> {
        return try {
            val response = httpClient.get("/repos/$owner/$name") {
                expectSuccess = false
            }
            when {
                response.status.isSuccess() -> {
                    try {
                        val dto = response.body<GithubRepoDetailsDto>()
                        Result.Success(dto.toDomain())
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Result.Failure(DeserializationException(e))
                    }
                }
                response.status == HttpStatusCode.NotFound -> {
                    Result.Failure(ApiCallException(SearchError.NotFound))
                }
                response.status == HttpStatusCode.Forbidden || response.status == HttpStatusCode.TooManyRequests -> {
                    Result.Failure(ApiCallException(SearchError.RateLimitExceeded))
                }
                response.status == HttpStatusCode.Unauthorized -> {
                    Result.Failure(ApiCallException(SearchError.Unauthorized))
                }
                response.status.value in 500..599 -> {
                    Result.Failure(ApiCallException(SearchError.ServerError))
                }
                else -> {
                    Result.Failure(ApiCallException(SearchError.UnknownApiError(response.status.value)))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Napier.e(tag = "KtorRepoDetailsApiService", throwable = e) { "Failed to fetch repo details for $owner/$name" }
            if (isNetworkException(e)) {
                Result.Failure(NetworkException(e))
            } else {
                Result.Failure(UnknownInfrastructureException(e))
            }
        }
    }
}
