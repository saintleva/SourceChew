package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.result.PagingSearchException
import com.github.saintleva.sourcechew.domain.result.Result
import com.jamal_aliev.paginator.Paginator
import com.jamal_aliev.paginator.dsl.paginator
import com.jamal_aliev.paginator.load.LoadResult
import kotlinx.coroutines.flow.first


class GetReposUseCaseImpl(
    private val configManager: ConfigManager,
    private val searchApiService: SearchApiService
) : GetReposUseCase {

    override suspend fun invoke(conditions: RepoSearchConditions): Paginator<FoundRepo> {
        val pageSize = configManager.appSettings.paginationPageSize.first()
        return paginator<FoundRepo>(capacity = pageSize) { //TODO: Is it right?
            load { page ->
                when (val result = searchApiService.searchItems(conditions, page, pageSize)) {
                    is Result.Success -> LoadResult(
                        data = result.value.items,
                        metadata = result.value.metadata,
                    )
                    is Result.Failure -> throw PagingSearchException(result.error)
                }
            }
        }
    }
}
