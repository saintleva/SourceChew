package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import com.github.saintleva.sourcechew.domain.repository.OwnerDetailsRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface GetOwnerDetailsUseCase {
    suspend operator fun invoke(
        login: String,
        forceRefresh: Boolean = false
    ): Result<OwnerDetailsBase, DetailsError>
}

class GetOwnerDetailsUseCaseImpl(
    private val repository: OwnerDetailsRepository
) : GetOwnerDetailsUseCase {
    override suspend fun invoke(
        login: String,
        forceRefresh: Boolean
    ): Result<OwnerDetailsBase, DetailsError> =
        repository.getOwnerDetails(login, forceRefresh)
}
