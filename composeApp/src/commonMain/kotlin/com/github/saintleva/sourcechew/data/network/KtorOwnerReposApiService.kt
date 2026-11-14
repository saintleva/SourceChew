package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.repository.OwnerReposRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

data class OwnerReposRequest(
    val owner: String,
    val page: Int,
    val pageSize: Int,
)

class KtorOwnerReposApiService(
    private val httpClient: HttpClient,
) : OwnerReposRepository,
    BaseKtorResourceApiService<OwnerReposRequest, List<FoundRepo>> {

    override suspend fun getHttpResponse(request: OwnerReposRequest): HttpResponse {
        return httpClient.get("/users/${request.owner}/repos") {
            parameter("page", request.page)
            parameter("per_page", request.pageSize)
            expectSuccess = false
        }
    }

    override suspend fun deserializeSuccess(response: HttpResponse): List<FoundRepo> {
        val dtos = response.body<List<GithubRepoDto>>()
        return dtos.map { it.toDomain() }
    }

    override suspend fun getOwnerRepos(
        owner: String,
        page: Int,
        pageSize: Int,
    ): Result<List<FoundRepo>, DetailsError> {
        return getAnswer(
            OwnerReposRequest(
                owner = owner,
                page = page,
                pageSize = pageSize,
            )
        )
    }
}
