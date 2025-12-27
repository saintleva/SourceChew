package com.github.saintleva.sourcechew.domain.usecase

import androidx.paging.PagingData
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RepoSearchInteractorImpl(
    val getReposUseCase: GetReposUseCase
) : RepoSearchInteractor {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.NeverSearched)
    override val searchState = _searchState.asStateFlow()

    override var previousConditions: RepoSearchConditions? = null
    override var previousResult: Flow<PagingData<FoundRepo>>? = null

    override suspend fun search(
        conditions: RepoSearchConditions,
        usePreviousSearch: Boolean
    ) {
        _searchState.update { SearchState.Searching }
        if (conditions == previousConditions) {
            if (usePreviousSearch) {
                usePreviousResult()
            } else {
                obtainNewResult(conditions)
            }
        } else {
            previousConditions = conditions
            obtainNewResult(conditions)
        }
    }

    override fun сanUsePreviousConditions(newConditions: RepoSearchConditions) =
        everSearched && (newConditions == previousConditions)

    private suspend fun obtainNewResult(conditions: RepoSearchConditions) {
        val result = getReposUseCase(conditions)
        previousResult = result
        _searchState.update { SearchState.Found(result) }
    }

    private fun usePreviousResult() {
        if (previousResult == null) {
            _searchState.update { SearchState.NeverSearched }
        } else {
            _searchState.update { SearchState.Found(previousResult!!) }
        }
    }
}