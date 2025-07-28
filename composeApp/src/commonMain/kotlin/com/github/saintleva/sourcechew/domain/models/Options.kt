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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


enum class RepoSearchScope {
    NAME, DESCRIPTION, README;

    companion object {
        val all = values().toList()
    }
}

enum class OnlyFlag {
    PUBLIC, PRIVATE, FORK, ARCHIVED, MIRROR, TEMPLATE;

    companion object {
        val all = values().toList()
    }
}

data class RepoSearchConditions(
    val query: String,
    val inScope: Set<RepoSearchScope>,
    val onlyFlags: Set<OnlyFlag>
) {
    companion object {
        val default = RepoSearchConditions(
            query = "",
            inScope = setOf(RepoSearchScope.NAME),
            onlyFlags = emptySet()
        )
    }
}

data class RepoSearchConditionsFlow(
    val query: Flow<String>,
    val inScope: Map<RepoSearchScope, Flow<Boolean>>,
    val onlyFlags: Map<OnlyFlag, Flow<Boolean>>,
    val usePreviousSearch: Flow<Boolean>
) {
    fun toConditionsStateFlow(
        scope: CoroutineScope,
        started: SharingStarted = SharingStarted.WhileSubscribed(5000)
    ) = RepoSearchConditionsStateFlow(
        query.stateIn(scope, started, initialValue = RepoSearchConditions.default.query),
        inScope.mapValues { it.value.stateIn(scope, started,
            initialValue = RepoSearchConditions.default.inScope.contains(it.key)) },
        onlyFlags.mapValues { it.value.stateIn(scope, started,
            initialValue = RepoSearchConditions.default.onlyFlags.contains(it.key)) },
        usePreviousSearch.stateIn(scope, started, initialValue = false)
    )

//    suspend fun toConditions() = RepoSearchConditions(
//        query.first(),
//        inScope.filter { it.value.first() }.keys,
//        onlyFlags.filter { it.value.first() }.keys
//    )
}

data class RepoSearchConditionsStateFlow(
    val query: StateFlow<String>,
    val inScope: Map<RepoSearchScope, StateFlow<Boolean>>,
    val onlyFlags: Map<OnlyFlag, StateFlow<Boolean>>,
    val usePreviousSearch: StateFlow<Boolean>
)