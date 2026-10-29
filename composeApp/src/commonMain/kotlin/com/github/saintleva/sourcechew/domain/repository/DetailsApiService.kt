package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.OwnerDetailsBase
import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface RepoDetailsApiService {
    suspend fun getRepoDetails(repoRef: RepoRef): Result<RepoDetails, DetailsError>
}

interface OwnerDetailsApiService {
    suspend fun getOwnerDetails(login: String): Result<OwnerDetailsBase, DetailsError>
}
