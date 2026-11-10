package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface MembersRepository {
    suspend fun getMembers(
        org: String,
        page: Int,
        pageSize: Int,
    ): Result<List<FoundOwner>, DetailsError>
}
