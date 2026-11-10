package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.repository.MembersRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

/**
 * Fake implementation of [MembersRepository] for testing.
 */
class FakeMembersRepository(
    var resultProvider: suspend (org: String, page: Int, pageSize: Int) -> Result<List<FoundOwner>, DetailsError> = { _, _, _ -> Result.Success(emptyList()) }
) : MembersRepository {
    override suspend fun getMembers(
        org: String,
        page: Int,
        pageSize: Int,
    ): Result<List<FoundOwner>, DetailsError> = resultProvider(org, page, pageSize)
}
