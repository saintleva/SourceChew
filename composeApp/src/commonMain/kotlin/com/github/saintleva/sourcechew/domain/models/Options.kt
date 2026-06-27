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

import kotlinx.serialization.Serializable

@Serializable
enum class RepoSearchScope {
    NAME, DESCRIPTION, README
}

@Serializable
enum class OnlyFlag {
    PUBLIC, PRIVATE, FORK, ARCHIVED, MIRROR, TEMPLATE
}

@Serializable
enum class RepoSearchSort {
    BEST_MATCH, STARS, FORKS, UPDATED;

    companion object {
        val default = BEST_MATCH
    }
}

@Serializable
enum class SearchOrder {
    ASCENDING, DESCENDING;

    companion object {
        val default = DESCENDING
    }
}

@Serializable
data class CommonFilters(
    val query: String,
    val order: SearchOrder
) {
    companion object {
        val default = CommonFilters(
            query = "",
            order = SearchOrder.default
        )
    }
}

@Serializable
data class RepoSearchConditions(
    val query: String,
    val inScope: Set<RepoSearchScope>,
    val onlyFlags: Set<OnlyFlag>,
    val sort: RepoSearchSort,
    val order: SearchOrder
) {

    fun maySearch(): Boolean {
        val allPrivacySelected = OnlyFlag.PUBLIC in onlyFlags && OnlyFlag.PRIVATE in onlyFlags
        return query.isNotBlank() && inScope.isNotEmpty() && !allPrivacySelected
    }

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