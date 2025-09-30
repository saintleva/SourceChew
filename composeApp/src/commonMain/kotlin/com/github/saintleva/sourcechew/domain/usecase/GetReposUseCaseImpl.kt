package com.github.saintleva.sourcechew.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.repository.PagingSourceFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetReposUseCaseImpl(
    private val pagingSourceFactory: PagingSourceFactory,
    private val configManager: ConfigManager
) : GetReposUseCase {

    override suspend fun invoke(conditions: RepoSearchConditions): Flow<PagingData<FoundRepo>> {
        return Pager(
            config = PagingConfig(
                pageSize = configManager.appSettings.paginationPageSize.first(),
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                pagingSourceFactory.createForRepoSearch(conditions)
            }
        ).flow
    }
}