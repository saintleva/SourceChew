package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.OwnerDetails
import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface RepoDetailsApiService {
    suspend fun getRepoDetails(owner: String, name: String): Result<RepoDetails, DetailsError>
}

interface OwnerDetailsApiService {
    suspend fun getOwnerDetails(login: String): Result<OwnerDetails, DetailsError>
}
