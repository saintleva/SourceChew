package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.di.ioDispatcher
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.jamal_aliev.paginator.bookmark.BookmarkInt
import com.jamal_aliev.paginator.page.PageState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


class RepoSearchInteractorImpl(
    private val getReposUseCase: GetReposUseCase,
    coroutineDispatcher: CoroutineDispatcher = ioDispatcher,
) : RepoSearchInteractor {

    private val scope = CoroutineScope(coroutineDispatcher + SupervisorJob())
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Selecting)
    override val searchState = _searchState.asStateFlow()

    private var previousConditions: RepoSearchConditions? = null
    private var lastFound: SearchState.Found? = null

    override val everSearched: Boolean
        get() = lastFound != null

    init {
        Napier.d(tag = "init") { "RepoSearchInteractor created: ${this.hashCode()}" }
    }

    override suspend fun search(
        conditions: RepoSearchConditions,
        usePreviousSearch: Boolean,
    ) {
        _searchState.update { SearchState.Searching }
        val cached = lastFound
        if (usePreviousSearch && cached != null && conditions == previousConditions) {
            Napier.d(tag = "search") {
                "Reusing cached paginator: ${cached.paginator.hashCode()}"
            }
            _searchState.update { cached }
        } else {
            obtainNewResult(conditions)
        }
    }

    override fun canUsePreviousConditions(newConditions: RepoSearchConditions): Boolean =
        everSearched && newConditions == previousConditions

    override fun switchToSelecting() {
        _searchState.update { SearchState.Selecting }
    }

    override fun clear() {
        lastFound?.paginator?.release()
        lastFound = null
        previousConditions = null
        scope.cancel()
    }

    private suspend fun obtainNewResult(conditions: RepoSearchConditions) {
        Napier.d(tag = "search") { "obtainNewResult($conditions)" }
        lastFound?.paginator?.let { previous ->
            Napier.d(tag = "search") { "Releasing previous paginator: ${previous.hashCode()}" }
            previous.release(silently = true)
        }

        val pager = getReposUseCase(conditions)
        val metadata: StateFlow<SearchMetadata?> = pager.core.snapshot
            .map { pages -> pages.firstSearchMetadata() }
            .stateIn(scope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

        val found = SearchState.Found(pager, metadata)
        previousConditions = conditions
        lastFound = found
        _searchState.update { found }
        Napier.d(tag = "search") { "Published Found(paginator=${pager.hashCode()})" }

        pager.jump(BookmarkInt(FIRST_PAGE))
    }

    private fun List<PageState<FoundRepo>>.firstSearchMetadata(): SearchMetadata? =
        firstNotNullOfOrNull { (it as? PageState.SuccessPage<FoundRepo>)?.metadata as? SearchMetadata }

    companion object {
        private const val FIRST_PAGE = 1
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
