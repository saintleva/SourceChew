package com.github.saintleva.sourcechew.data.paging

import androidx.paging.PagingSource
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.PagingSourceFactory
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.usecase.Totality
import kotlinx.coroutines.flow.MutableStateFlow


class PagingSourceFactoryImpl(private val apiService: SearchApiService) : PagingSourceFactory {

    override fun createForRepoSearch(
        conditions: RepoSearchConditions,
        totalityState: MutableStateFlow<Totality?>,
    ): PagingSource<Int, FoundRepo> =
        SearchPagingSource(apiService, conditions, totalityState)
}