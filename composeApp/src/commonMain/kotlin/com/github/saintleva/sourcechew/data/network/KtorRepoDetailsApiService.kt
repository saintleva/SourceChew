package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.models.RepoLanguage
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsApiService
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class RepoDetailsRequest(val owner: String, val name: String)

class KtorRepoDetailsApiService(
    private val httpClient: HttpClient
) : RepoDetailsApiService,
    BaseKtorResourceApiService<RepoDetailsRequest, RepoDetails> {

    override suspend fun getHttpResponse(request: RepoDetailsRequest): HttpResponse {
        return httpClient.get("/repos/${request.owner}/${request.name}") {
            expectSuccess = false
        }
    }

    override suspend fun deserializeSuccess(response: HttpResponse): RepoDetails {
        val dto = response.body<GithubRepoDetailsDto>()
        return dto.toDomain()
    }

    override suspend fun getRepoDetails(owner: String, name: String): Result<RepoDetails, DetailsError> {
        return coroutineScope {
            val languagesDeferred = async {
                fetchLanguages(owner, name)
            }
            val detailsResult = getAnswer(RepoDetailsRequest(owner, name))
            if (detailsResult is Result.Success) {
                val languages = languagesDeferred.await()
                Result.Success(detailsResult.value.copy(languages = languages))
            } else {
                languagesDeferred.cancel()
                detailsResult
            }
        }
    }

    private suspend fun fetchLanguages(owner: String, name: String): List<RepoLanguage> {
        return try {
            val response = httpClient.get("/repos/$owner/$name/languages") {
                expectSuccess = false
            }
            if (response.status.isSuccess()) {
                val map = response.body<Map<String, Long>>()
                map.toRepoLanguages()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
