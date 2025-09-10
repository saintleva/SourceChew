package com.github.saintleva.sourcechew.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.repository.SearchApiService


class SearchPagingSource(
    private val searchApiService: SearchApiService,
    private val conditions: RepoSearchConditions,
    private val searchOrder: SearchOrder,
): PagingSource<Int, FoundRepo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoundRepo> {
        val currentPageNumber = params.key ?: 1
        val response = searchApiService.search(
    }

    override fun getRefreshKey(state: PagingState<Int, FoundRepo>): Int? {
        TODO("Not yet implemented")
    }
}