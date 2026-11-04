package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.repository.BranchesApiService
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsApiService
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.PagingBranchesException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import com.jamal_aliev.paginator.offset.dsl.paginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first


class FetchBranchesUseCaseImpl(
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val branchesApiService: BranchesApiService,
    private val repoDetailsApiService: RepoDetailsApiService
) : FetchBranchesUseCase {

    override suspend fun invoke(repoRef: RepoRef): BranchData = coroutineScope {
        val defaultResultDeferred = async {
            // Safely fetch default branch details, catching both Result failures and thrown exceptions
            try {
                when (val repoDetailsResult = repoDetailsApiService.getRepoDetails(repoRef)) {
                    is Result.Success -> {
                        val defaultBranchName = repoDetailsResult.value.defaultBranch
                        if (defaultBranchName == null) {
                            BranchDetailsResult.Success(null)
                        } else {
                            when (val branchResult = branchesApiService.getSingleBranch(repoRef, defaultBranchName)) {
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
                when (val result = branchesApiService.getBranches(repoRef, page, pageSize)) {
                    is Result.Success -> LoadResult(data = result.value)
                    is Result.Failure -> throw PagingBranchesException(result.error)
                }
            }
        }

        BranchData(defaultResult, paginator)
    }
}
