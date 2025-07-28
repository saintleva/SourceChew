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

import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.repository.SearchRepository
import kotlinx.coroutines.flow.first

class FindUseCaseImpl(
    private val configManager: ConfigManager,
    private val searchRepository: SearchRepository,
) : FindUseCase {

    //TODO: May I use there first() terminal operator of Flow or I need to switch to StateFlow?
    override suspend fun invoke(conditions: RepoSearchConditions) {
        if (conditions != configManager.previousRepoConditions.first()) {
            configRepository.changeRepoPreviousConditions(conditions)
            searchRepository.search(conditions)
        } else if (configRepository.usePreviousRepoSearch.first()) {
            searchRepository.usePreviousResult()
        } else {
            searchRepository.search(conditions)
        }
    }
}