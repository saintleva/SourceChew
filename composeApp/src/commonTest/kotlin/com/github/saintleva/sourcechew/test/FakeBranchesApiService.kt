package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.BranchesApiService
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

/**
 * Fake implementation of [BranchesApiService] for testing.
 */
class FakeBranchesApiService(
    var resultProvider: suspend (repoRef: RepoRef, page: Int, pageSize: Int) -> Result<List<Branch>, DetailsError>
) : BranchesApiService {
    override suspend fun getBranches(
        repoRef: RepoRef,
        page: Int,
        pageSize: Int,
    ): Result<List<Branch>, DetailsError> = resultProvider(repoRef, page, pageSize)

    override suspend fun getSingleBranch(
        repoRef: RepoRef,
        branch: String,
    ): Result<Branch, DetailsError> = Result.Failure(DetailsError.ResourceNotFound)
}
