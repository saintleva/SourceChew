package com.github.saintleva.sourcechew.data.remote

import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter


class KtorApiService(
    private val httpClient: HttpClient
): SearchApiService {

    companion object {
        const val BASE_URL = "https://api.github.com/search/repositories"
    }

    override suspend fun searchItems(
        conditions: RepoSearchConditions,
        sort: String,
        order: String,
        page: Int,
        pageSize: Int
    ): SearchResponseDto {
        return httpClient.get(BASE_URL) {
            parameter("q", conditions.query)
            parameter("sort", sort)
            parameter("order", order)
            parameter("page", page)
            parameter("per_page", pageSize)
        }.body()
    }
}