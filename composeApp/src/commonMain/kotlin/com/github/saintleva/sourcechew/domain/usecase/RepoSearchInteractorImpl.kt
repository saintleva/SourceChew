package com.github.saintleva.sourcechew.domain.usecase

import androidx.paging.PagingData
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import io.github.aakira.napier.Napier
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

    init {
        Napier.d(tag = "init") { "RepoSearchInteractor created: ${this.hashCode()}" }
    }

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

    override fun canUsePreviousConditions(newConditions: RepoSearchConditions) =
        everSearched && (newConditions == previousConditions)

    override fun switchToSelecting() {
        _searchState.update { SearchState.Selecting }
    }

    private suspend fun obtainNewResult(conditions: RepoSearchConditions) {
        val result = getReposUseCase(conditions)
        previousResult = result
        _searchState.update { SearchState.Found(result) }
        Napier.d(tag = "RepoSearchInteractorImpl") { "_searchState updated. Value is ${_searchState.value}" }
    }

    private fun usePreviousResult() {
        if (previousResult == null) {
            Napier.d(tag = "RepoSearchInteractorImpl") {"usePreviousResult() called and previousResult == null" }
            _searchState.update { SearchState.Selecting }
        } else {
            _searchState.update { SearchState.Found(previousResult!!) }
        }
    }
}