package com.github.saintleva.sourcechew.data.repository

import com.github.saintleva.sourcechew.data.network.RepoDetailsApiService
import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsRepository
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.Result
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RepoDetailsRepositoryImpl(
    private val apiService: RepoDetailsApiService
) : RepoDetailsRepository {

    private val cache = mutableMapOf<String, RepoDetails>()
    private val mutex = Mutex()

    override suspend fun getRepoDetails(
        owner: String,
        name: String,
        forceRefresh: Boolean
    ): Result<RepoDetails, AppException> {
        val cacheKey = "${owner.lowercase()}/${name.lowercase()}"
        if (!forceRefresh) {
            mutex.withLock {
                val cached = cache[cacheKey]
                if (cached != null) return Result.Success(cached)
            }
        }

        val result = apiService.getRepoDetails(owner, name)
        if (result is Result.Success) {
            mutex.withLock {
                cache[cacheKey] = result.value
            }
        }
        return result
    }
}
