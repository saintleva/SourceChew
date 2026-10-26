package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.RepoBranch
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface RepoBranchesApiService {
    suspend fun getBranches(
        owner: String,
        repo: String,
        page: Int,
        pageSize: Int,
    ): Result<List<RepoBranch>, DetailsError>
}
