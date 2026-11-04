package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsApiService
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

class FakeRepoDetailsApiService(
    var resultProvider: (RepoRef) -> Result<RepoDetails, DetailsError> = { Result.Failure(DetailsError.ResourceNotFound) }
) : RepoDetailsApiService {
    override suspend fun getRepoDetails(repoRef: RepoRef): Result<RepoDetails, DetailsError> = resultProvider(repoRef)
}
