package com.github.saintleva.sourcechew.data.repository

import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import com.github.saintleva.sourcechew.domain.repository.OwnerDetailsApiService
import com.github.saintleva.sourcechew.domain.repository.OwnerDetailsRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OwnerDetailsRepositoryImpl(
    private val apiService: OwnerDetailsApiService
) : OwnerDetailsRepository {

    private val cache = mutableMapOf<String, OwnerDetailsBase>()
    private val mutex = Mutex()

    override suspend fun getOwnerDetails(
        login: String,
        forceRefresh: Boolean
    ): Result<OwnerDetailsBase, DetailsError> {
        val cacheKey = login.lowercase()
        if (!forceRefresh) {
            mutex.withLock {
                val cached = cache[cacheKey]
                if (cached != null) return Result.Success(cached)
            }
        }

        val result = apiService.getOwnerDetails(login)
        if (result is Result.Success) {
            mutex.withLock {
                cache[cacheKey] = result.value
            }
        }
        return result
    }
}
