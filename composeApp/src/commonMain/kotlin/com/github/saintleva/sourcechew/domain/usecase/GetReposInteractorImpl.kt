package com.github.saintleva.sourcechew.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.repository.PagingSourceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

class GetReposInteractorImpl(
    private val pagingSourceFactory: PagingSourceFactory,
    private val configManager: ConfigManager
) : GetReposInteractor {

    override val totalityState = MutableStateFlow<Totality?>(null)

    override suspend fun getRepos(conditions: RepoSearchConditions): PaginatedRepos {
        return PaginatedRepos(
            totality = totalityState,
            data = Pager(
                config = PagingConfig(
                    pageSize = configManager.appSettings.paginationPageSize.first(),
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    pagingSourceFactory.createForRepoSearch(conditions, totalityState)
                }
            ).flow
        )
    }
}