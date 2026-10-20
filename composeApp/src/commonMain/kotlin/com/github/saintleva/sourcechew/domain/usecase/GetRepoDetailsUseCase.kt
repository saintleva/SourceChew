package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface GetRepoDetailsUseCase {
    suspend operator fun invoke(
        owner: String,
        name: String,
        forceRefresh: Boolean = false
    ): Result<RepoDetails, DetailsError>
}

class GetRepoDetailsUseCaseImpl(
    private val repository: RepoDetailsRepository
) : GetRepoDetailsUseCase {
    override suspend fun invoke(
        owner: String,
        name: String,
        forceRefresh: Boolean
    ): Result<RepoDetails, DetailsError> =
        repository.getRepoDetails(owner, name, forceRefresh)
}
