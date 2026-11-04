package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.DetailsBase
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import com.jamal_aliev.paginator.offset.Paginator

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
