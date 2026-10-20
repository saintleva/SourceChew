package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsApiService
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

data class RepoDetailsRequest(val owner: String, val name: String)

class KtorRepoDetailsApiService(
    private val httpClient: HttpClient
) : RepoDetailsApiService,
    BaseKtorRestApiService<RepoDetailsRequest, RepoDetails, DetailsError> {

    override suspend fun getHttpResponse(request: RepoDetailsRequest): HttpResponse {
        return httpClient.get("/repos/${request.owner}/${request.name}") {
            expectSuccess = false
        }
    }

    override suspend fun deserializeSuccess(response: HttpResponse): RepoDetails {
        val dto = response.body<GithubRepoDetailsDto>()
        return dto.toDomain()
    }

    override suspend fun mapErrorResponse(response: HttpResponse): DetailsError {
        return defaultMapCommonApiError(response)
    }

    override suspend fun getRepoDetails(owner: String, name: String): Result<RepoDetails, DetailsError> {
        return getAnswer(RepoDetailsRequest(owner, name))
    }
}
