package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.repository.UserOrgsRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

/**
 * Fake implementation of [UserOrgsRepository] for testing.
 */
class FakeUserOrgsRepository(
    var resultProvider: suspend (username: String, page: Int, pageSize: Int) -> Result<List<Owner>, DetailsError> = { _, _, _ -> Result.Success(emptyList()) }
) : UserOrgsRepository {
    override suspend fun getUserOrgs(
        username: String,
        page: Int,
        pageSize: Int,
    ): Result<List<Owner>, DetailsError> = resultProvider(username, page, pageSize)
}
