package com.github.saintleva.sourcechew.data.paging

import androidx.paging.PagingSource
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.PagingSourceFactory
import com.github.saintleva.sourcechew.domain.repository.SearchApiService

class PagingSourceFactoryImpl(private val apiService: SearchApiService) : PagingSourceFactory {

    override fun createForRepoSearch(conditions: RepoSearchConditions): PagingSource<Int, FoundRepo> {
        return SearchPagingSource(apiService, conditions)
    }
}