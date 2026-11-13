package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface UserOrgsRepository {
    suspend fun getUserOrgs(
        username: String,
        page: Int,
        pageSize: Int,
    ): Result<List<Owner>, DetailsError>
}
