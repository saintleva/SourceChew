package com.github.saintleva.sourcechew.domain.usecase

import androidx.paging.PagingData
import com.github.saintleva.sourcechew.domain.NeverSearchedException
import com.github.saintleva.sourcechew.domain.models.FetchParams
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RepoSearchInteractorImpl(
    val getReposUseCase: GetReposUseCase
) : RepoSearchInteractor {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Selecting)
    override val searchState = _searchState.asStateFlow()

    override var previousConditions: RepoSearchConditions? = null
    override var previousResult: Flow<PagingData<FoundRepo>>? = null

    override suspend fun search(
        conditions: RepoSearchConditions,
        fetchParams: FetchParams,
        usePreviousSearch: Boolean
    ) {
        _searchState.update { SearchState.Searching }
        if (conditions == previousConditions) {
            if (usePreviousSearch) {
                usePreviousResult()
            } else {
                obtainNewResult(conditions, fetchParams)
            }
        } else {
            previousConditions = conditions
            obtainNewResult(conditions, fetchParams)
        }
    }

    override fun сanUsePreviousConditions(newConditions: RepoSearchConditions) =
        everSearched && (newConditions == previousConditions)

    override fun switchToSelecting() {
        _searchState.update { SearchState.Selecting }
    }

    private suspend fun obtainNewResult(conditions: RepoSearchConditions, fetchParams: FetchParams) {
        val result = getReposUseCase(conditions, fetchParams)
        previousResult = result
        _searchState.update { SearchState.Success(result) }
    }

    private suspend fun usePreviousResult() {
        if (previousResult == null) {
            _searchState.update { SearchState.Error(NeverSearchedException()) }
        } else {
            _searchState.update { SearchState.Success(previousResult!!) }
        }
    }
}