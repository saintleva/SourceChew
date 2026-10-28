package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.repository.BranchesApiService
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

data class BranchesRequest(
    val owner: String,
    val repo: String,
    val page: Int,
    val pageSize: Int,
)

data class SingleBranchRequest(
    val owner: String,
    val repo: String,
    val branch: String,
)

class KtorBranchesApiService(
    private val httpClient: HttpClient,
) : BranchesApiService {

    private val branchesService = object : BaseKtorResourceApiService<BranchesRequest, List<Branch>> {

        override suspend fun getHttpResponse(request: BranchesRequest): HttpResponse {
            return httpClient.get("/repos/${request.owner}/${request.repo}/branches") {
                parameter("page", request.page)
                parameter("per_page", request.pageSize)
                expectSuccess = false
            }
        }

        override suspend fun deserializeSuccess(response: HttpResponse): List<Branch> {
            val dtos = response.body<List<GithubBranchDto>>()
            return dtos.map { it.toDomain() }
        }
    }

    private val branchService = object : BaseKtorResourceApiService<SingleBranchRequest, Branch> {
        override suspend fun getHttpResponse(request: SingleBranchRequest): HttpResponse {
            return httpClient.get("/repos/${request.owner}/${request.repo}/branches/${request.branch}") {
                expectSuccess = false
            }
        }

        override suspend fun deserializeSuccess(response: HttpResponse): Branch {
            val dto = response.body<GithubBranchDto>()
            return dto.toDomain()
        }
    }

    override suspend fun getBranches(
        owner: String,
        repo: String,
        page: Int,
        pageSize: Int,
    ): Result<List<Branch>, DetailsError> {
        return branchesService.getAnswer(
            BranchesRequest(
                owner = owner,
                repo = repo,
                page = page,
                pageSize = pageSize,
            ),
        )
    }

    override suspend fun getSingleBranch(
        owner: String,
        repo: String,
        branch: String,
    ): Result<Branch, DetailsError> {
        return branchService.getAnswer(
            SingleBranchRequest(
                owner = owner,
                repo = repo,
                branch = branch,
            ),
        )
    }
}
