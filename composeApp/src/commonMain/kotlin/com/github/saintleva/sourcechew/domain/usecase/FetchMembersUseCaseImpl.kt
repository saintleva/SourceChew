package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.repository.MembersRepository
import com.github.saintleva.sourcechew.domain.result.PagingMembersException
import com.github.saintleva.sourcechew.domain.result.Result
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.dsl.paginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import kotlinx.coroutines.flow.first

class FetchMembersUseCaseImpl(
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val membersRepository: MembersRepository,
) : FetchMembersUseCase {

    override suspend fun invoke(org: String): Paginator<FoundOwner> {
        val pageSize = appSettingsStore.config.first().paginationPageSize
        return paginator(capacity = pageSize) {
            load { page ->
                when (val result = membersRepository.getMembers(org, page, pageSize)) {
                    is Result.Success -> LoadResult(data = result.value)
                    is Result.Failure -> throw PagingMembersException(result.error)
                }
            }
        }
    }
}
