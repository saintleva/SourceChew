package com.github.saintleva.sourcechew.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.result.PagingSearchException
import com.github.saintleva.sourcechew.domain.result.Result


class SearchPagingSource(
    private val searchApiService: SearchApiService,
    private val conditions: RepoSearchConditions
): PagingSource<Int, FoundRepo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoundRepo> {
        val pageIndex = params.key ?: 1
        val pageSize = params.loadSize
        return try {
            when (val result = searchApiService.searchItems(conditions, pageIndex, pageSize)) {
                is Result.Success -> {
                    LoadResult.Page(
                        data = result.value,
                        prevKey = if (pageIndex == 1) null else pageIndex - 1,
                        nextKey = if (result.value.isEmpty()) null else pageIndex + 1
                    )
                }
                is Result.Failure -> {
                    LoadResult.Error(PagingSearchException(result.error))
                }
            }
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