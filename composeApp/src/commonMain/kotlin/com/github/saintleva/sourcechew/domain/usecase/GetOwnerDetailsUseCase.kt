package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.OwnerDetails
import com.github.saintleva.sourcechew.domain.repository.OwnerDetailsRepository
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.Result

interface GetOwnerDetailsUseCase {
    suspend operator fun invoke(
        login: String,
        forceRefresh: Boolean = false
    ): Result<OwnerDetails, AppException>
}

class GetOwnerDetailsUseCaseImpl(
    private val repository: OwnerDetailsRepository
) : GetOwnerDetailsUseCase {
    override suspend fun invoke(
        login: String,
        forceRefresh: Boolean
    ): Result<OwnerDetails, AppException> =
        repository.getOwnerDetails(login, forceRefresh)
}
