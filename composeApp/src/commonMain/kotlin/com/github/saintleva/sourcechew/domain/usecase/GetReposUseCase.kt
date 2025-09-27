package com.github.saintleva.sourcechew.domain.usecase

import androidx.paging.PagingData
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import kotlinx.coroutines.flow.Flow


interface GetReposUseCase {
    suspend operator fun invoke(conditions: RepoSearchConditions): Flow<PagingData<FoundRepo>>
}