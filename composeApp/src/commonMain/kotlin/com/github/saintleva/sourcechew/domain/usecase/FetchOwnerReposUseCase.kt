package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.repository.OwnerReposRepository
import com.github.saintleva.sourcechew.domain.result.PagingOwnerReposException
import com.github.saintleva.sourcechew.domain.result.Result
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.dsl.paginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import kotlinx.coroutines.flow.first

interface FetchOwnerReposUseCase {
    suspend operator fun invoke(owner: String): Paginator<FoundRepo>
}

class FetchOwnerReposUseCaseImpl(
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val ownerReposRepository: OwnerReposRepository,
) : FetchOwnerReposUseCase {

    override suspend fun invoke(owner: String): Paginator<FoundRepo> {
        val pageSize = appSettingsStore.config.first().paginationPageSize
        return paginator(capacity = pageSize) {
            load { page ->
                when (val result = ownerReposRepository.getOwnerRepos(owner, page, pageSize)) {
                    is Result.Success -> LoadResult(data = result.value)
                    is Result.Failure -> throw PagingOwnerReposException(result.error)
                }
            }
        }
    }
}
