package com.github.saintleva.sourcechew.data.remote

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FoundRepoDto(
    val author: String,
    val name: String,
    val description: String,
    val language: String,
    val stars: Int
)

fun FoundRepoDto.toDomain() = FoundRepo(
    author = author,
    name = name,
    description = description,
    language = language,
    stars = stars
)

@Serializable
data class SearchResponseDto(
    @SerialName("total_count") val totalCount: Int,
    val items: List<FoundRepoDto>
)

fun SearchResponseDto.toDomain() = items.map { it.toDomain() }

class KtorRestApiService(
    private val httpClient: HttpClient,
    private val searchDispatcher: CoroutineDispatcher = Dispatchers.IO
): SearchApiService {

    companion object {
        const val BASE_URL = "https://api.github.com/search/repositories"

        val sortVariants = mapOf(
            RepoSearchSort.BEST_MATCH to "best match", //TODO: Is it right?
            RepoSearchSort.STARS to "stars",
            RepoSearchSort.FORKS to "forks",
            RepoSearchSort.UPDATED to "updated"
        )

        val orderVariants = mapOf(
            SearchOrder.ASCENDING to "asc",
            SearchOrder.DESCENDING to "desc"
        )
    }

    override suspend fun searchItems(
        conditions: RepoSearchConditions,
        page: Int,
        pageSize: Int
    ): List<FoundRepo> {
        val dto: SearchResponseDto = httpClient.get(BASE_URL) {
            parameter("q", conditions.query)
            parameter("sort", sortVariants[conditions.sort]!!)
            parameter("order", orderVariants[conditions.order]!!)
            parameter("page", page)
            parameter("per_page", pageSize)
        }.body()
        return dto.toDomain()
    }
}