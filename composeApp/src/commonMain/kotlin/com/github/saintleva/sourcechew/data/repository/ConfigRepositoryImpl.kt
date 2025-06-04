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
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext


class ConfigRepositoryImpl(
    private val configManager: ConfigManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ConfigRepository {

    override val previousConditions = MutableSharedFlow<SearchConditions>(replay = 1)
    override val usePreviousSearch = MutableSharedFlow<Boolean>(replay = 1)

    override suspend fun loadData() {
        previousConditions.emit(configManager.loadPreviousConditions())
        usePreviousSearch.emit(configManager.loadUsePreviousSearch())
    }

    override suspend fun changePreviousConditions(newValue: SearchConditions) {
        withContext(ioDispatcher) {
            configManager.savePreviousConditions(newValue)
            previousConditions.emit(newValue) //TODO: Do I really need this?
        }
    }

    override suspend fun changeUsePreviousSearch(newValue: Boolean) {
        withContext(ioDispatcher) {
            configManager.saveUsePreviousSearch(newValue)
            usePreviousSearch.emit(newValue) //TODO: Do I really need this?
        }
    }
}