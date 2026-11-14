package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.DetailsBase
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.BranchesRepository
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsRepository
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.PagingBranchesException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.dsl.paginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

sealed interface BranchDetailsResult {
    data class Success(val value: Branch?) : BranchDetailsResult
    data class Error(val error: DetailsError) : BranchDetailsResult
    data class InfrastructureError(val cause: AppException) : BranchDetailsResult
}

class BranchData(
    val default: BranchDetailsResult,
    val paginator: Paginator<Branch>
)

interface FetchBranchesUseCase {
    suspend operator fun invoke(repoRef: RepoRef): BranchData
}

class FetchBranchesUseCaseImpl(
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val branchesRepository: BranchesRepository,
    private val repoDetailsRepository: RepoDetailsRepository
) : FetchBranchesUseCase {

    override suspend fun invoke(repoRef: RepoRef): BranchData = coroutineScope {
        val defaultResultDeferred = async {
            // Safely fetch default branch details, catching both Result failures and thrown exceptions
            try {
                when (val repoDetailsResult = repoDetailsRepository.getRepoDetails(repoRef, forceRefresh = true)) {
                    is Result.Success -> {
                        val defaultBranchName = repoDetailsResult.value.defaultBranch
                        if (defaultBranchName == null) {
                            BranchDetailsResult.Success(null)
                        } else {
                            when (val branchResult = branchesRepository.getSingleBranch(repoRef, defaultBranchName)) {
                                is Result.Success -> BranchDetailsResult.Success(branchResult.value)
                                is Result.Failure -> BranchDetailsResult.Error(branchResult.error)
                            }
                        }
                    }
                    is Result.Failure -> BranchDetailsResult.Error(repoDetailsResult.error)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: AppException) {
                BranchDetailsResult.InfrastructureError(e)
            } catch (e: Exception) {
                BranchDetailsResult.InfrastructureError(UnknownInfrastructureException(e))
            }
        }

        val pageSizeDeferred = async {
            appSettingsStore.config.first().paginationPageSize
        }

        val defaultResult = defaultResultDeferred.await()
        val pageSize = pageSizeDeferred.await()

        val paginator = paginator(capacity = pageSize) {
            load { page ->
                when (val result = branchesRepository.getBranches(repoRef, page, pageSize)) {
                    is Result.Success -> LoadResult(data = result.value)
                    is Result.Failure -> throw PagingBranchesException(result.error)
                }
            }
        }

        BranchData(defaultResult, paginator)
    }
}
