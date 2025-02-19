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

import com.github.saintleva.sourcechew.data.storage.ConfigManager
import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.models.TypeOptions
import com.github.saintleva.sourcechew.domain.models.defaultForgeOptions
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


class ConfigRepositoryImpl(
    private val configManager: ConfigManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ConfigRepository {

    override var previousConditions: SearchConditions =
        SearchConditions(
            defaultForgeOptions,
            TypeOptions(repo = true, user = false, group = false),
            ""
        )

    override var usePreviousSearch = false
    //TODO: remove it
    //override var previousConditionsHasBeenUsed = false

    override suspend fun loadData() {
        previousConditions = configManager.loadPreviousConditions()
        usePreviousSearch = configManager.loadUsePreviousSearch()
    }

    override suspend fun changePreviousConditions(newValue: SearchConditions) {
        withContext(ioDispatcher) {
            configManager.savePreviousConditions(newValue)
            previousConditions = newValue
        }
    }

    override suspend fun changeUsePreviousSearch(newValue: Boolean) {
        withContext(ioDispatcher) {
            configManager.saveUsePreviousSearch(newValue)
            usePreviousSearch = newValue
        }
    }
}