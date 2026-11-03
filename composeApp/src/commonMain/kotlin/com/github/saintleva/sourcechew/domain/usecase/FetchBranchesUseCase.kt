package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.jamal_aliev.paginator.offset.Paginator

class BranchPaginating(
    val default: Branch?,
    val paginator: Paginator<Branch>
)

interface FetchBranchesUseCase {
    suspend operator fun invoke(repoRef: RepoRef, defaultBranch: String?): BranchPaginating
}
