package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface RepoDetailsRepository {
    suspend fun getRepoDetails(
        repoRef: RepoRef,
        forceRefresh: Boolean = false
    ): Result<RepoDetails, DetailsError>
}
