package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface OwnerReposRepository {
    suspend fun getOwnerRepos(
        owner: String,
        page: Int,
        pageSize: Int,
    ): Result<List<FoundRepo>, DetailsError>
}
