package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface OwnerDetailsRepository {
    suspend fun getOwnerDetails(
        login: String,
        forceRefresh: Boolean = false
    ): Result<OwnerDetailsBase, DetailsError>
}
