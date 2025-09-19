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

package com.github.saintleva.sourcechew.domain.models

import com.github.saintleva.sourcechew.domain.utils.makeSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


enum class RepoSearchScope {
    NAME, DESCRIPTION, README
}

enum class OnlyFlag {
    PUBLIC, PRIVATE, FORK, ARCHIVED, MIRROR, TEMPLATE
}

enum class RepoSearchSort(val code: Int) {
    BEST_MATCH(0), STARS(1), FORKS(2), UPDATED(3);

    companion object {
        val default = BEST_MATCH
        fun fromCode(code: Int) = entries.find { it.code == code } ?: default
    }
}

enum class SearchOrder(val code: Int) {
    ASCENDING(0), DESCENDING(1);

    companion object {
        val default = DESCENDING
        fun fromCode(code: Int) = entries.find { it.code == code } ?: default
    }
}

data class RepoSearchConditions(
    val query: String,
    val inScope: Set<RepoSearchScope>,
    val onlyFlags: Set<OnlyFlag>,
    val sort: RepoSearchSort,
    val order: SearchOrder
) {
    companion object {
        val default = RepoSearchConditions(
            query = "",
            inScope = setOf(RepoSearchScope.NAME),
            onlyFlags = emptySet(),
            sort = RepoSearchSort.BEST_MATCH,
            order = SearchOrder.default
        )
    }
}

class RepoSearchConditionsFlows(
    val query: Flow<String>,
    val inScope: Map<RepoSearchScope, Flow<Boolean>>,
    val onlyFlags: Map<OnlyFlag, Flow<Boolean>>,
    val sort: Flow<RepoSearchSort>,
    val order: Flow<SearchOrder>,
    val usePreviousSearch: Flow<Boolean>
) {
    fun toConditionsStateFlow(
        scope: CoroutineScope,
        started: SharingStarted = SharingStarted.WhileSubscribed(5000)
    ) = RepoSearchConditionsStateFlows(
        query.stateIn(scope, started, initialValue = RepoSearchConditions.default.query),
        inScope.mapValues { it.value.stateIn(scope, started,
            initialValue = it.key in RepoSearchConditions.default.inScope) },
        onlyFlags.mapValues { it.value.stateIn(scope, started,
            initialValue = it.key in RepoSearchConditions.default.onlyFlags) },
        sort.stateIn(scope, started, initialValue = RepoSearchConditions.default.sort),
        order.stateIn(scope, started, initialValue = RepoSearchConditions.default.order),
        usePreviousSearch.stateIn(scope, started, initialValue = false)
    )
}

class RepoSearchConditionsStateFlows(
    val query: StateFlow<String>,
    val inScope: Map<RepoSearchScope, StateFlow<Boolean>>,
    val onlyFlags: Map<OnlyFlag, StateFlow<Boolean>>,
    val sort: StateFlow<RepoSearchSort>,
    val order: StateFlow<SearchOrder>,
    val usePreviousSearch: StateFlow<Boolean>
) {
        fun toConditions() = RepoSearchConditions(
        query.value,
        inScope.makeSet { it.value },
        onlyFlags.makeSet { it.value },
            sort.value,
            order.value
    )
}

data class FetchConfig(
    val pageSize: Int
)

val defaultPaginationPageSize = 30