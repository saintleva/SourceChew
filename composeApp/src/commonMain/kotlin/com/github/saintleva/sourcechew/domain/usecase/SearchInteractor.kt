/*
 * Copyright (C) Anton Liaukevich 2021-2022 <leva.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.coroutines.ioDispatcher
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.pagination.FIRST_PAGE
import com.github.saintleva.sourcechew.domain.repository.AuthRepository
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface SearchState<out FoundItem: FoundBase> {
    data object Selecting : SearchState<Nothing>
    data object Searching : SearchState<Nothing>
    data class Found<out FoundItem: FoundBase>(
        val paginator: Paginator<@UnsafeVariance FoundItem>
    ) : SearchState<FoundItem>

}

interface SearchInteractor<ItemSearchConditions, out FoundItem: FoundBase> {

    val searchState: StateFlow<SearchState<FoundItem>>

    val currentPaginator: Paginator<@UnsafeVariance FoundItem>?
        get() = (searchState.value as? SearchState.Found)?.paginator

    val everSearched: Boolean

    suspend fun search(conditions: ItemSearchConditions, usePreviousSearch: Boolean)

    fun canUsePreviousConditions(newConditions: ItemSearchConditions): Boolean

    fun switchToSelecting()

    fun clear()
}

class SearchInteractorImpl<ItemSearchConditions, out FoundItem: FoundBase>(
    private val fetchFoundItemsUseCase: FetchFoundItemsUseCase<ItemSearchConditions, FoundItem>,
    authRepository: AuthRepository,
    appSettingsStore: ConfigStore<AppSettings>,
    coroutineDispatcher: CoroutineDispatcher = ioDispatcher
) : SearchInteractor<ItemSearchConditions, FoundItem> {

    private val scope = CoroutineScope(coroutineDispatcher + SupervisorJob())

    private val _searchState = MutableStateFlow<SearchState<FoundItem>>(SearchState.Selecting)
    override val searchState = _searchState.asStateFlow()

    private var previousConditions: ItemSearchConditions? = null
    private var lastFound: Paginator<@UnsafeVariance FoundItem>? = null

    override val currentPaginator: Paginator<@UnsafeVariance FoundItem>?
        get() = lastFound

    override val everSearched: Boolean
        get() = lastFound != null

    init {
        Napier.d(tag = "init") { "SearchInteractor created: ${this.hashCode()}" }
        scope.launch {
            authRepository.authToken
                .drop(1)
                .collect {
                    Napier.d(tag = "search") { "Auth token changed, clearing search interactor" }
                    clear()
                }
        }
        scope.launch {
            appSettingsStore.config
                .map { it.paginationPageSize }
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    Napier.d(tag = "search") { "Page size changed, clearing search interactor" }
                    clear()
                }
        }
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
        lastFound?.let {
            Napier.d(tag = "search") { "Releasing paginator on clear: ${it.hashCode()}" }
            it.release(silently = true)
        }
        lastFound = null
        previousConditions = null
        _searchState.update { SearchState.Selecting }
    }

    private suspend fun obtainNewResult(conditions: ItemSearchConditions) {
        Napier.d(tag = "search") { "obtainNewResult($conditions)" }
        lastFound?.let { previous ->
            Napier.d(tag = "search") { "Releasing previous paginator: ${previous.hashCode()}" }
            previous.release(silently = true)
        }

        val paginator = fetchFoundItemsUseCase(conditions)
        previousConditions = conditions
        lastFound = paginator
        _searchState.update { SearchState.Found(paginator) }
        Napier.d(tag = "search") { "Published Found(paginator=${paginator.hashCode()})" }

        paginator.jump(BookmarkInt(FIRST_PAGE))
    }
}
