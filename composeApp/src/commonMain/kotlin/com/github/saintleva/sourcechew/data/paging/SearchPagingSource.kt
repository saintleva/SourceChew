package com.github.saintleva.sourcechew.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.result.RepoSearchResult
import com.github.saintleva.sourcechew.domain.result.SearchError


class PagingSearchException(val error: SearchError)

class SearchPagingSource(
    private val searchApiService: SearchApiService,
    private val conditions: RepoSearchConditions
): PagingSource<Int, FoundRepo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoundRepo> {
        val pageIndex = params.key ?: 1
        val pageSize = params.loadSize
        return try {
            val result = searchApiService.searchItems(conditions, pageIndex, pageSize)
            when (result) {
                is RepoSearchResult.Success -> {
                    val repos = result.value
                }
            }
            LoadResult.Page(
                data = response as List<FoundRepo>,
                prevKey = if (pageIndex == 1) null else pageIndex - 1,
                nextKey = if (response.isEmpty()) null else pageIndex + 1
            )
        } catch (e: Exception) {
            // This block now correctly catches all exceptions:
            //    - PagingDomainException (for business errors like API limits)
            //    - NetworkException, DeserializationException (for infrastructure errors)
            //    - Any other unexpected exception.
            // This signals to the Paging library that the load attempt failed.
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