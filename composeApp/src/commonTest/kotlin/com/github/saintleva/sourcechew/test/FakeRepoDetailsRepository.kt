package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

class FakeRepoDetailsRepository(
    var resultProvider: (RepoRef) -> Result<RepoDetails, DetailsError> = { Result.Failure(DetailsError.ResourceNotFound) }
) : RepoDetailsRepository {
    override suspend fun getRepoDetails(
        repoRef: RepoRef,
        forceRefresh: Boolean
    ): Result<RepoDetails, DetailsError> = resultProvider(repoRef)
}
