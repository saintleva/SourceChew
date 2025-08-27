package com.github.saintleva.sourcechew.data.remote

import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
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

@Serializable
data class SearchResponseDto(
    @SerialName("total_count") val totalCount: Int,
    val items: List<FoundRepoDto>
)

interface SearchApiService {
    suspend fun searchItems(
        conditions: RepoSearchConditions,
        sort: String,
        order: String,
        page: Int,
        pageSize: Int
    ): SearchResponseDto
}