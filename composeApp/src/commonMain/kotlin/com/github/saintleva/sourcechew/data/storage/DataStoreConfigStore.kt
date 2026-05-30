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

package com.github.saintleva.sourcechew.data.storage

import androidx.datastore.core.DataStore
import com.github.saintleva.sourcechew.domain.repository.ConfigLens
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class DataStoreConfigStore<T>(
    private val dataStore: DataStore<AppPreferences>,
    private val lens: ConfigLens<AppPreferences, T>
) : ConfigStore<T> {

    override val config: Flow<T> = dataStore.data.map { lens.get(it) }

    override suspend fun update(updaterBlock: (T) -> T) {
        dataStore.updateData { currentPrefs ->
            val currentT = lens.get(currentPrefs)
            val updatedT = updaterBlock(currentT)
            lens.set(currentPrefs, updatedT)
        }
    }
}