package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.OwnerDetails
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.Result

interface OwnerDetailsRepository {
    suspend fun getOwnerDetails(
        login: String,
        forceRefresh: Boolean = false
    ): Result<OwnerDetails, AppException>
}
