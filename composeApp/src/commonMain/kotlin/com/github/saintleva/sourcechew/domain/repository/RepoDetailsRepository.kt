package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface RepoDetailsRepository {
    suspend fun getRepoDetails(
        owner: String,
        name: String,
        forceRefresh: Boolean = false
    ): Result<RepoDetails, DetailsError>
}
