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

import com.github.saintleva.sourcechew.domain.models.FoundItems
import com.github.saintleva.sourcechew.domain.models.Item
import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.repository.StandardSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration


class SearchRepositoryStub(
    private val eachCount: Int,
    private val delayImitation: Duration = Duration.ZERO,
    private val searchDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StandardSearchRepository() {

    override suspend fun find(conditions: SearchConditions): FoundItems {
        val result = FoundItems()
        withContext(searchDispatcher) {
            for (forgeOption in conditions.forgeOptions) {
                if (forgeOption.value) {
                    val forge = forgeOption.key
                    if (conditions.typeOptions.repo) {
                        for (i in 0 until eachCount) {
                            delay(delayImitation)
                            result.repos.add(Item.Repo(forge, "${i}${conditions.text}${i}"))
                        }
                    }
                    if (conditions.typeOptions.user) {
                        for (i in 0 until eachCount) {
                            delay(delayImitation)
                            result.users.add(Item.User(forge, "${i}${conditions.text}${i}"))
                        }
                    }
                    if (conditions.typeOptions.group) {
                        for (i in 0 until eachCount) {
                            delay(delayImitation)
                            result.groups.add(Item.Group(forge, "${i}${conditions.text}${i}"))
                        }
                    }
                }
            }
        }
        return result
    }
}