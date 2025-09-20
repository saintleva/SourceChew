package com.github.saintleva.sourcechew.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.saintleva.sourcechew.data.paging.SearchPagingSource
import com.github.saintleva.sourcechew.domain.models.FetchParams
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetReposUseCaseImpl(
    private val apiService: SearchApiService,
    private val configManager: ConfigManager
) : GetReposUseCase {

    override suspend fun invoke(
        conditions: RepoSearchConditions,
        fetchParams: FetchParams
    ): Flow<PagingData<FoundRepo>> {
        return Pager(
            config = PagingConfig(
                pageSize = configManager.appSettings.paginationPageSize.first(),
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                SearchPagingSource(apiService, conditions)
            }
        ).flow
    }
}