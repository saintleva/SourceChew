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

package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditionsFlow
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope


interface ConfigManager {

    val previousRepoConditions: RepoSearchConditionsFlow

    interface RepoSearchConditionsSaver {
        suspend fun saveQuery(query: String)
        suspend fun saveScopeItem(item: RepoSearchScope)
        suspend fun saveOnlyFlag(onlyFlag: OnlyFlag)
        suspend fun saveUsePreviousSearch(value: Boolean)
    }

    val repoSearchConditionsSaver: RepoSearchConditionsSaver
}