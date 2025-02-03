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
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.domain.models.ForgeOptions
import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.models.TypeOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class DataStoreConfig(private val dataStore: DataStore<Preferences>) : ConfigStorage {

    private companion object {

        private const val PREVIOUS_CONDITIONS = "PreviousConditions"

        object PreviousConditionsTypeOptionsKeys {
            val repo = booleanPreferencesKey("${PREVIOUS_CONDITIONS}_repo")
            val user = booleanPreferencesKey("${PREVIOUS_CONDITIONS}_user")
            val group = booleanPreferencesKey("${PREVIOUS_CONDITIONS}_group")
        }
        val forgeOptionsKeys = Forge.list.associateWith {
            booleanPreferencesKey("${PREVIOUS_CONDITIONS}_forge_${it.name}")
        }

        val usePreviousSearchKey = booleanPreferencesKey("usePreviousSearch")
    }

    override suspend fun savePreviousConditions(value: SearchConditions) {
        dataStore.edit { preferences ->
            preferences[PreviousConditionsTypeOptionsKeys.repo] = value.typeOptions.repo
            preferences[PreviousConditionsTypeOptionsKeys.user] = value.typeOptions.user
            preferences[PreviousConditionsTypeOptionsKeys.group] = value.typeOptions.group
            forgeOptionsKeys.forEach { entry ->
                preferences[entry.value] = value.forgeOptions[entry.key]!!
            }
        }
    }

    override suspend fun loadPreviousConditions(): SearchConditions {
        val typeOptions = TypeOptions(
            repo = dataStore.data.map {
                preferences -> preferences[PreviousConditionsTypeOptionsKeys.repo] ?: false
            }.first(),
            user = dataStore.data.map {
                    preferences -> preferences[PreviousConditionsTypeOptionsKeys.user] ?: false
            }.first(),
            group = dataStore.data.map {
                    preferences -> preferences[PreviousConditionsTypeOptionsKeys.group] ?: false
            }.first()
        )

    }


    override suspend fun saveUsePreviousSearch(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[usePreviousSearchKey] = value
        }
    }

    override suspend fun loadUsePreviousSearch(): Boolean =
        dataStore.data.map { preferences -> preferences[usePreviousSearchKey] ?: false }.first()
}