package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.models.BaseSearchConditions
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Fake implementation of [SearchInteractor] for testing.
 */
class FakeSearchInteractor<C : BaseSearchConditions<C>, I : FoundBase> : SearchInteractor<C, I> {
    override val searchState: StateFlow<SearchState<I>> = MutableStateFlow(SearchState.Selecting)
    override val everSearched: Boolean = false
    override var lastScrollPosition: ScrollPosition? = null
    override suspend fun search(conditions: C, usePreviousSearch: Boolean) {}
    override fun canUsePreviousConditions(newConditions: C): Boolean = false
    override fun switchToSelecting() {}
    override fun clear() {}
}
