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

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable


@Serializable
enum class OwnerType {
    USER, ORGANIZATION
}

@Serializable
enum class RepoSearchScope {
    NAME, DESCRIPTION, README
}

@Serializable
enum class OwnerSearchScope {
    LOGIN, FULLNAME, EMAIL
}
@Serializable
enum class RepoOnlyFlag {
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
enum class OwnerSearchSort {
    BEST_MATCH, FOLLOWERS, REPOSITORIES, JOINED;

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

interface HasCommonFilters<T: HasCommonFilters<T>> {
    val common: CommonFilters
    fun withCommon(common: CommonFilters): T
}

@Serializable
data class RepoSearchConditions(
    override val common: CommonFilters,
    val inScope: Set<RepoSearchScope>,
    val onlyFlags: Set<RepoOnlyFlag>,
    val sort: RepoSearchSort
) : HasCommonFilters<RepoSearchConditions> {

    fun maySearch(): Boolean {
        val allPrivacySelected = RepoOnlyFlag.PUBLIC in onlyFlags && RepoOnlyFlag.PRIVATE in onlyFlags
        return inScope.isNotEmpty() && !allPrivacySelected
    }

    override fun withCommon(common: CommonFilters): RepoSearchConditions {
        return copy(common = common)
    }

    companion object {
        val default = RepoSearchConditions(
            common = CommonFilters.default,
            inScope = setOf(RepoSearchScope.NAME),
            onlyFlags = emptySet(),
            sort = RepoSearchSort.BEST_MATCH
        )
    }
}

@Serializable
data class OwnerSearchConditions(
    override val common: CommonFilters,
    val inScope: Set<OwnerSearchScope>,
    val sort: OwnerSearchSort,
    val repos: IntFilter?,
    val followers: IntFilter?,
    val location: String?
) : HasCommonFilters<OwnerSearchConditions> {

    fun maySearch(): Boolean {
        return inScope.isNotEmpty()
    }

    override fun withCommon(common: CommonFilters): OwnerSearchConditions {
        return copy(common = common)
    }

    companion object {
        val default = OwnerSearchConditions(
            common = CommonFilters.default,
            inScope = setOf(OwnerSearchScope.LOGIN),
            sort = OwnerSearchSort.BEST_MATCH,
            repos = null,
            followers = null,
            location = null
        )
    }
}

fun <C : HasCommonFilters<C>> MutableStateFlow<C>.updateCommonFilters(
    transform: (CommonFilters) -> CommonFilters
) {
    this.update { current ->
        current.withCommon(transform(current.common))
    }
}