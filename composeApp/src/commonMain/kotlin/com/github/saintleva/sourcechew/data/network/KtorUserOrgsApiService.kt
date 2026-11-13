package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.repository.UserOrgsRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

data class UserOrgsRequest(
    val username: String,
    val page: Int,
    val pageSize: Int,
)

class KtorUserOrgsApiService(
    private val httpClient: HttpClient,
) : UserOrgsRepository,
    BaseKtorResourceApiService<UserOrgsRequest, List<Owner>> {

    override suspend fun getHttpResponse(request: UserOrgsRequest): HttpResponse {
        return httpClient.get("/users/${request.username}/orgs") {
            parameter("page", request.page)
            parameter("per_page", request.pageSize)
            expectSuccess = false
        }
    }

    override suspend fun deserializeSuccess(response: HttpResponse): List<Owner> {
        val dtos = response.body<List<GithubOrgDto>>()
        return dtos.map { it.toDomain() }
    }

    override suspend fun getUserOrgs(
        username: String,
        page: Int,
        pageSize: Int,
    ): Result<List<Owner>, DetailsError> {
        return getAnswer(
            UserOrgsRequest(
                username = username,
                page = page,
                pageSize = pageSize,
            )
        )
    }
}
