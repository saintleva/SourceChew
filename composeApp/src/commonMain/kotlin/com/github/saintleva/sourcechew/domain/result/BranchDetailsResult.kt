package com.github.saintleva.sourcechew.domain.result

import com.github.saintleva.sourcechew.domain.models.Branch

sealed interface BranchDetailsResult {
    data class Success(val value: Branch) : BranchDetailsResult
    data object NonExistent : BranchDetailsResult
    data class Error(val error: DetailsError) : BranchDetailsResult
    data class InfrastructureError(val cause: AppException) : BranchDetailsResult
}
