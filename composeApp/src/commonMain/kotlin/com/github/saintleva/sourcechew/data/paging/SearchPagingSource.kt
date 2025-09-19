package com.github.saintleva.sourcechew.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.SearchApiService


class SearchPagingSource(
    private val searchApiService: SearchApiService,
    private val conditions: RepoSearchConditions
): PagingSource<Int, FoundRepo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoundRepo> {
        val pageIndex = params.key ?: 1
        val pageSize = params.loadSize
        return try {
            val response = searchApiService.searchItems(conditions, pageIndex, pageSize)
            LoadResult.Page(
                data = response,
                prevKey = if (pageIndex == 1) null else pageIndex - 1,
                nextKey = if (response.isEmpty()) null else pageIndex + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FoundRepo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}