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

package com.github.saintleva.sourcechew.data.repository

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.repository.StandardSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration


class SearchRepositoryStub(
    configManager: ConfigManager,
    private val eachCount: Int,
    private val delayImitation: Duration = Duration.ZERO,
    private val searchDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StandardSearchRepository() {

    override suspend fun find(conditions: RepoSearchConditions): List<FoundRepo> {

        fun name(i: Int) = "${i}${conditions.query}${i}"

        val result = mutableListOf<FoundRepo>()
        withContext(searchDispatcher) {
            for (inScope in conditions.inScope) {
                for (i in 0 until eachCount) {
                    delay(delayImitation)
                    result.add(FoundRepo(name(i), name(i), inScope.name, name(i), i))
                }
            }
        }

        return result
    }
}