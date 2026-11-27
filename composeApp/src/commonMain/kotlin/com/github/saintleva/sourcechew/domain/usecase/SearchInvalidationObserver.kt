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

package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.coroutines.ioDispatcher
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.repository.AuthRepository
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface SearchInvalidationObserver

class SearchInvalidationObserverImpl(
    authRepository: AuthRepository,
    appSettingsStore: ConfigStore<AppSettings>,
    searchInteractors: List<SearchInteractor<*, *>>,
    coroutineDispatcher: CoroutineDispatcher = ioDispatcher
) : SearchInvalidationObserver {

    private val scope = CoroutineScope(coroutineDispatcher + SupervisorJob())

    init {
        scope.launch {
            authRepository.authToken
                .drop(1)
                .collect {
                    Napier.d(tag = "search") { "Auth token changed, clearing search interactors" }
                    searchInteractors.forEach { it.clear() }
                }
        }
        scope.launch {
            appSettingsStore.config
                .map { it.paginationPageSize }
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    Napier.d(tag = "search") { "Page size changed, clearing search interactors" }
                    searchInteractors.forEach { it.clear() }
                }
        }
    }
}
