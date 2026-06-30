package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.di.ioDispatcher
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SearchInteractorImpl<ItemSearchConditions, out FoundItem: FoundBase>(
    private val fetchItemsUseCase: FetchItemsUseCase<ItemSearchConditions, FoundItem>,
    coroutineDispatcher: CoroutineDispatcher = ioDispatcher
) : SearchInteractor<ItemSearchConditions, FoundItem> {

    private val scope = CoroutineScope(coroutineDispatcher + SupervisorJob())
    private val _searchState = MutableStateFlow<SearchState<FoundItem>>(SearchState.Selecting)
    override val searchState = _searchState.asStateFlow()

    private var previousConditions: ItemSearchConditions? = null
    private var lastFound: Paginator<FoundItem>? = null

    override var lastScrollPosition: ScrollPosition? = null

    override val everSearched: Boolean
        get() = lastFound != null

    init {
        Napier.d(tag = "init") { "SearchInteractor created: ${this.hashCode()}" }
    }

    override suspend fun search(conditions: ItemSearchConditions, usePreviousSearch: Boolean) {
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

    override fun canUsePreviousConditions(newConditions: ItemSearchConditions): Boolean =
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

    private suspend fun obtainNewResult(conditions: ItemSearchConditions) {
        Napier.d(tag = "search") { "obtainNewResult($conditions)" }
        lastFound?.let { previous ->
            Napier.d(tag = "search") { "Releasing previous paginator: ${previous.hashCode()}" }
            previous.release(silently = true)
        }

        val paginator = fetchItemsUseCase(conditions)
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
