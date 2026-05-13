package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.jamal_aliev.paginator.load.Metadata
import com.jamal_aliev.paginator.page.PaginatorUiState
import kotlinx.coroutines.flow.Flow


class ExtendedPaginatorUiState<T>(
    val uiState: PaginatorUiState<T>,
    val metadata: SearchMetadata
)

typealias PaginationFlow = Flow<ExtendedPaginatorUiState<FoundRepo>>

interface GetReposUseCase {
    suspend operator fun invoke(conditions: RepoSearchConditions): PaginationFlow
}