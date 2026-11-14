package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.repository.OwnerReposRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

/**
 * Fake implementation of [OwnerReposRepository] for testing.
 */
class FakeOwnerReposRepository(
    var resultProvider: suspend (owner: String, page: Int, pageSize: Int) -> Result<List<FoundRepo>, DetailsError> = { _, _, _ -> Result.Success(emptyList()) }
) : OwnerReposRepository {
    override suspend fun getOwnerRepos(
        owner: String,
        page: Int,
        pageSize: Int,
    ): Result<List<FoundRepo>, DetailsError> = resultProvider(owner, page, pageSize)
}
