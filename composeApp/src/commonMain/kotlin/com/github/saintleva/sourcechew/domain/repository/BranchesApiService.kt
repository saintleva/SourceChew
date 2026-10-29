package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface BranchesApiService {
    suspend fun getBranches(
        repoRef: RepoRef,
        page: Int,
        pageSize: Int,
    ): Result<List<Branch>, DetailsError>

    suspend fun getSingleBranch(
        repoRef: RepoRef,
        branch: String,
    ): Result<Branch, DetailsError>
}
