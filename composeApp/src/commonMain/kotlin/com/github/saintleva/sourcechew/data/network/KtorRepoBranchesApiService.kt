package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.RepoBranch
import com.github.saintleva.sourcechew.domain.repository.RepoBranchesApiService
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

data class RepoBranchesRequest(
    val owner: String,
    val repo: String,
    val page: Int,
    val pageSize: Int,
)

class KtorRepoBranchesApiService(
    private val httpClient: HttpClient,
) : RepoBranchesApiService,
    BaseKtorApiService<RepoBranchesRequest, List<RepoBranch>, DetailsError> {

    override suspend fun getHttpResponse(request: RepoBranchesRequest): HttpResponse {
        return httpClient.get("/repos/${request.owner}/${request.repo}/branches") {
            parameter("page", request.page)
            parameter("per_page", request.pageSize)
            expectSuccess = false
        }
    }

    override suspend fun deserializeSuccess(response: HttpResponse): List<RepoBranch> {
        val dtos = response.body<List<GithubBranchDto>>()
        return dtos.map { it.toDomain() }
    }

    override suspend fun mapErrorResponse(response: HttpResponse): DetailsError {
        return if (response.status == HttpStatusCode.NotFound) {
            DetailsError.ResourceNotFound
        } else {
            mapCommonApiError(response)
        }
    }

    override suspend fun getBranches(
        owner: String,
        repo: String,
        page: Int,
        pageSize: Int,
    ): Result<List<RepoBranch>, DetailsError> {
        return getAnswer(
            RepoBranchesRequest(
                owner = owner,
                repo = repo,
                page = page,
                pageSize = pageSize,
            ),
        )
    }
}
