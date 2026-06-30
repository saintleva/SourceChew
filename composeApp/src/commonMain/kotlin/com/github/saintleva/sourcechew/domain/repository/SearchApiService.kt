package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.domain.result.SearchResult


class FoundItemsBlock<out FoundItem: FoundBase>(
    val items: List<FoundItem>,
    val metadata: SearchMetadata
)

interface SearchApiService<ItemSearchConditions, out FoundItem: FoundBase> {
    suspend fun searchItems(
        conditions: ItemSearchConditions,
        page: Int,
        pageSize: Int
    ): SearchResult<FoundItemsBlock<FoundItem>>
}