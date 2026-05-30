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