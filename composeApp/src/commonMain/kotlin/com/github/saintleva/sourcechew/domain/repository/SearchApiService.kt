package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.result.SearchResult


interface SearchApiService {
    suspend fun searchItems(
        conditions: RepoSearchConditions,
        page: Int,
        pageSize: Int
    ): SearchResult<List<FoundRepo>>
}