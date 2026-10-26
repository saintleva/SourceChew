package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.RepoBranch
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.repository.RepoBranchesApiService
import com.github.saintleva.sourcechew.domain.result.PagingBranchesException
import com.github.saintleva.sourcechew.domain.result.Result
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.dsl.paginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import kotlinx.coroutines.flow.first

class FetchBranchesUseCaseImpl(
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val branchesApiService: RepoBranchesApiService
) : FetchBranchesUseCase {

    override suspend fun invoke(owner: String, repo: String): Paginator<RepoBranch> {
        val pageSize = appSettingsStore.config.first().paginationPageSize
        return paginator(capacity = pageSize) {
            load { page ->
                when (val result = branchesApiService.getBranches(owner, repo, page, pageSize)) {
                    is Result.Success -> LoadResult(data = result.value)
                    is Result.Failure -> throw PagingBranchesException(result.error)
                }
            }
        }
    }
}
