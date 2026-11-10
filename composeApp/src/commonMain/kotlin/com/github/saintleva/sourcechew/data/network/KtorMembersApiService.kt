package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.repository.MembersRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

data class MembersRequest(
    val org: String,
    val page: Int,
    val pageSize: Int,
)

class KtorMembersApiService(
    private val httpClient: HttpClient,
) : MembersRepository,
    BaseKtorResourceApiService<MembersRequest, List<FoundOwner>> {

    override suspend fun getHttpResponse(request: MembersRequest): HttpResponse {
        return httpClient.get("/orgs/${request.org}/members") {
            parameter("page", request.page)
            parameter("per_page", request.pageSize)
            expectSuccess = false
        }
    }

    override suspend fun deserializeSuccess(response: HttpResponse): List<FoundOwner> {
        val dtos = response.body<List<GithubMemberDto>>()
        return dtos.map { it.toDomain() }
    }

    override suspend fun getMembers(
        org: String,
        page: Int,
        pageSize: Int,
    ): Result<List<FoundOwner>, DetailsError> {
        return getAnswer(
            MembersRequest(
                org = org,
                page = page,
                pageSize = pageSize,
            )
        )
    }
}
