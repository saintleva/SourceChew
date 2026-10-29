package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.models.RepoLanguage
import com.github.saintleva.sourcechew.domain.models.RepoRef
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

class KtorRepoDetailsApiService(
    private val httpClient: HttpClient
) : RepoDetailsApiService,
    BaseKtorResourceApiService<RepoRef, RepoDetails> {

    override suspend fun getHttpResponse(request: RepoRef): HttpResponse {
        return httpClient.get("/repos/${request.owner}/${request.name}") {
            expectSuccess = false
        }
    }

    override suspend fun deserializeSuccess(response: HttpResponse): RepoDetails {
        val dto = response.body<GithubRepoDetailsDto>()
        return dto.toDomain()
    }

    override suspend fun getRepoDetails(repoRef: RepoRef): Result<RepoDetails, DetailsError> {
        return coroutineScope {
            val languagesDeferred = async {
                fetchLanguages(repoRef)
            }
            val detailsResult = getAnswer(repoRef)
            if (detailsResult is Result.Success) {
                val languages = languagesDeferred.await()
                Result.Success(detailsResult.value.copy(languages = languages))
            } else {
                languagesDeferred.cancel()
                detailsResult
            }
        }
    }

    private suspend fun fetchLanguages(repoRef: RepoRef): List<RepoLanguage> {
        return try {
            val response = httpClient.get("/repos/${repoRef.owner}/${repoRef.name}/languages") {
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
