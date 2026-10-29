package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.RepoRef
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
    val repoRef: RepoRef,
    val page: Int,
    val pageSize: Int,
)

data class SingleBranchRequest(
    val repoRef: RepoRef,
    val branch: String,
)

class KtorBranchesApiService(
    private val httpClient: HttpClient,
) : BranchesApiService {

    private val branchesService = object : BaseKtorResourceApiService<BranchesRequest, List<Branch>> {

        override suspend fun getHttpResponse(request: BranchesRequest): HttpResponse {
            return httpClient.get("/repos/${request.repoRef.owner}/${request.repoRef.name}/branches") {
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

    private val singleBranchService = object : BaseKtorResourceApiService<SingleBranchRequest, Branch> {
        override suspend fun getHttpResponse(request: SingleBranchRequest): HttpResponse {
            return httpClient.get("/repos/${request.repoRef.owner}/${request.repoRef.name}/branches/${request.branch}") {
                expectSuccess = false
            }
        }

        override suspend fun deserializeSuccess(response: HttpResponse): Branch {
            val dto = response.body<GithubBranchDto>()
            return dto.toDomain()
        }
    }

    override suspend fun getBranches(
        repoRef: RepoRef,
        page: Int,
        pageSize: Int,
    ): Result<List<Branch>, DetailsError> {
        return branchesService.getAnswer(
            BranchesRequest(
                repoRef = repoRef,
                page = page,
                pageSize = pageSize,
            ),
        )
    }

    override suspend fun getSingleBranch(
        repoRef: RepoRef,
        branch: String,
    ): Result<Branch, DetailsError> {
        return singleBranchService.getAnswer(
            SingleBranchRequest(
                repoRef = repoRef,
                branch = branch,
            ),
        )
    }
}
