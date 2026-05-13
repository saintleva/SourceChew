package com.github.saintleva.sourcechew.domain.usecase

import androidx.paging.PagingData
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class Totality (
    count: Int,
    incomplete: Boolean
)

class PaginatedRepos(
    val totality: Flow<Totality?>,
    val data: Flow<PagingData<FoundRepo>>
)

interface GetReposInteractor {

    val totalityStateFlow: MutableStateFlow<Totality?>

    suspend fun getRepos(conditions: RepoSearchConditions): PaginatedRepos
}