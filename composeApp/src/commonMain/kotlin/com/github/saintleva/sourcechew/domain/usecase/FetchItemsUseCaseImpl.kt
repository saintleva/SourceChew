package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.result.PagingSearchException
import com.github.saintleva.sourcechew.domain.result.Result
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.dsl.paginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import kotlinx.coroutines.flow.first


class FetchItemsUseCaseImpl<ItemSearchConditions, out FoundItem: FoundBase>(
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val searchApiService: SearchApiService<ItemSearchConditions, FoundItem>
) : FetchItemsUseCase<ItemSearchConditions, FoundItem> {

    override suspend fun invoke(conditions: ItemSearchConditions): Paginator<out FoundItem> {
        val pageSize = appSettingsStore.config.first().paginationPageSize
        return paginator(capacity = pageSize) {
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
