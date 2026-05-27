package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.di.ioDispatcher
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import com.jamal_aliev.paginator.core.page.PageState
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
    coroutineDispatcher: CoroutineDispatcher = ioDispatcher
) : RepoSearchInteractor {

    private val scope = CoroutineScope(coroutineDispatcher + SupervisorJob())
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Selecting)
    override val searchState = _searchState.asStateFlow()

    private var previousConditions: RepoSearchConditions? = null
    private var lastFound: Paginator<FoundRepo>? = null

    override var lastScrollPosition: ScrollPosition? = null

    override val everSearched: Boolean
        get() = lastFound != null

    init {
        Napier.d(tag = "init") { "RepoSearchInteractor created: ${this.hashCode()}" }
    }

    override suspend fun search(
        conditions: RepoSearchConditions,
        usePreviousSearch: Boolean
    ) {
        _searchState.update { SearchState.Searching }
        val cached = lastFound
        if (usePreviousSearch && cached != null && conditions == previousConditions) {
            Napier.d(tag = "search") {
                "Reusing cached paginator: ${cached.hashCode()}"
            }
            _searchState.update { SearchState.Found(cached) }
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
        lastFound?.release()
        lastFound = null
        previousConditions = null
        lastScrollPosition = null
        scope.cancel()
    }

    private suspend fun obtainNewResult(conditions: RepoSearchConditions) {
        Napier.d(tag = "search") { "obtainNewResult($conditions)" }
        lastFound?.let { previous ->
            Napier.d(tag = "search") { "Releasing previous paginator: ${previous.hashCode()}" }
            previous.release(silently = true)
        }

        val paginator = getReposUseCase(conditions)
        previousConditions = conditions
        lastFound = paginator
        lastScrollPosition = null
        _searchState.update { SearchState.Found(paginator) }
        Napier.d(tag = "search") { "Published Found(paginator=${paginator.hashCode()})" }

        paginator.jump(BookmarkInt(FIRST_PAGE))
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}
