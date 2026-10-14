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
import kotlinx.datetime.LocalDate
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
    val query: String = "",
    val created: Range<LocalDate>? = null,
    val order: SearchOrder = SearchOrder.default
) {
    companion object {
        val default = CommonFilters()
    }
}

interface HasCommonFilters<T: HasCommonFilters<T>> {
    val common: CommonFilters
    fun withCommon(common: CommonFilters): T

    val query: String
        get() = common.query

    val created: Range<LocalDate>?
        get() = common.created

    val order: SearchOrder
        get() = common.order
}

interface BaseSearchConditions<T : BaseSearchConditions<T>> : HasCommonFilters<T> {
    fun maySearch(): Boolean
}

@Serializable
data class RepoSearchConditions(
    override val common: CommonFilters = CommonFilters.default,
    val inScope: Set<RepoSearchScope> = setOf(RepoSearchScope.NAME),
    val onlyFlags: Set<RepoOnlyFlag> = emptySet(),
    val sort: RepoSearchSort = RepoSearchSort.default,
    val stars: Range<Int>? = null,
    val forks: Range<Int>? = null,
    val size: Range<Int>? = null,
    val pushed: Range<LocalDate>? = null,
    val owners: Set<String> = emptySet(),
    val languages: Set<String> = emptySet(),
    val topics: Set<String> = emptySet(),
    val licenses: Set<String> = emptySet()
) : BaseSearchConditions<RepoSearchConditions> {

    override fun maySearch(): Boolean {
        val allPrivacySelected = RepoOnlyFlag.PUBLIC in onlyFlags && RepoOnlyFlag.PRIVATE in onlyFlags
        return inScope.isNotEmpty() && !allPrivacySelected
    }

    override fun withCommon(common: CommonFilters): RepoSearchConditions {
        return copy(common = common)
    }

    companion object {
        val default = RepoSearchConditions()
    }
}

@Serializable
data class OwnerSearchConditions(
    override val common: CommonFilters = CommonFilters.default,
    val inScope: Set<OwnerSearchScope> = setOf(OwnerSearchScope.LOGIN),
    val types: Set<OwnerType> = setOf(OwnerType.USER, OwnerType.ORGANIZATION),
    val sort: OwnerSearchSort = OwnerSearchSort.default,
    val repos: Range<Int>? = null,
    val followers: Range<Int>? = null,
    val locations: Set<String> = emptySet()
) : BaseSearchConditions<OwnerSearchConditions> {

    override fun maySearch(): Boolean {
        return inScope.isNotEmpty() && types.isNotEmpty()
    }

    override fun withCommon(common: CommonFilters): OwnerSearchConditions {
        return copy(common = common)
    }

    companion object {
        val default = OwnerSearchConditions()
    }
}

fun <C : HasCommonFilters<C>> MutableStateFlow<C>.updateCommonFilters(
    transform: (CommonFilters) -> CommonFilters
) {
    this.update { current ->
        current.withCommon(transform(current.common))
    }
}