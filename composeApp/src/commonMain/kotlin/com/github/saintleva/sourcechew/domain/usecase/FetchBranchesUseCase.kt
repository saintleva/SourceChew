package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.jamal_aliev.paginator.offset.Paginator

interface FetchBranchesUseCase {
    suspend operator fun invoke(repoRef: RepoRef): Paginator<Branch>
}
