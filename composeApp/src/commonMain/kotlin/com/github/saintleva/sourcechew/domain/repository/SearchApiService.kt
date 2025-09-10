package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.SearchOrder


interface SearchApiService {
    suspend fun searchItems(
        conditions: RepoSearchConditions,
        order: SearchOrder,
        page: Int,
        pageSize: Int
    ): List<FoundRepo>
}