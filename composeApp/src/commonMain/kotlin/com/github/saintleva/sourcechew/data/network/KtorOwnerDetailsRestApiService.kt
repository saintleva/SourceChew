package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.OwnerDetails
import com.github.saintleva.sourcechew.domain.repository.OwnerDetailsApiService
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class KtorOwnerDetailsApiService(
    private val httpClient: HttpClient
) : OwnerDetailsApiService,
    BaseKtorRestApiService<String, OwnerDetails, DetailsError> {

    override suspend fun getHttpResponse(request: String): HttpResponse {
        return httpClient.get("/users/$request") {
            expectSuccess = false
        }
    }

    override suspend fun deserializeSuccess(response: HttpResponse): OwnerDetails {
        val dto = response.body<GithubOwnerDetailsDto>()
        return dto.toDomain()
    }

    override suspend fun mapErrorResponse(response: HttpResponse): DetailsError {
        return defaultMapCommonApiError(response)
    }

    override suspend fun getOwnerDetails(login: String): Result<OwnerDetails, DetailsError> {
        return getAnswer(login)
    }
}
