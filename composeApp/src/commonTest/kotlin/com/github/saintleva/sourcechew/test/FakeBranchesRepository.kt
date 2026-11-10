package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.BranchesRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

/**
 * Fake implementation of [BranchesRepository] for testing.
 */
class FakeBranchesRepository(
    var resultProvider: suspend (repoRef: RepoRef, page: Int, pageSize: Int) -> Result<List<Branch>, DetailsError> = { _, _, _ -> Result.Success(emptyList()) },
    var singleBranchResultProvider: suspend (repoRef: RepoRef, branch: String) -> Result<Branch, DetailsError> = { _, _ -> Result.Failure(DetailsError.ResourceNotFound) }
) : BranchesRepository {
    override suspend fun getBranches(
        repoRef: RepoRef,
        page: Int,
        pageSize: Int,
    ): Result<List<Branch>, DetailsError> = resultProvider(repoRef, page, pageSize)

    override suspend fun getSingleBranch(
        repoRef: RepoRef,
        branch: String,
    ): Result<Branch, DetailsError> = singleBranchResultProvider(repoRef, branch)
}
