package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.repository.UserOrgsRepository
import com.github.saintleva.sourcechew.domain.result.PagingUserOrgsException
import com.github.saintleva.sourcechew.domain.result.Result
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.dsl.paginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import kotlinx.coroutines.flow.first

class FetchUserOrgsUseCaseImpl(
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val userOrgsRepository: UserOrgsRepository,
) : FetchUserOrgsUseCase {

    override suspend fun invoke(username: String): Paginator<Owner> {
        val pageSize = appSettingsStore.config.first().paginationPageSize
        return paginator(capacity = pageSize) {
            load { page ->
                when (val result = userOrgsRepository.getUserOrgs(username, page, pageSize)) {
                    is Result.Success -> LoadResult(data = result.value)
                    is Result.Failure -> throw PagingUserOrgsException(result.error)
                }
            }
        }
    }
}
