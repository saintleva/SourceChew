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
import com.github.saintleva.sourcechew.domain.models.SearchConditions


class DataStoreConfig(private val dataStore: DataStore<Preferences>) : ConfigStorage {

    private companion object {
        const val PREVIOUS_CONDITIONS = "PreviousConditions"
        const val REPO = "repo"
        const val USER = "user"
        const val GROUP = "group"
        const val USE_PREVIOUS_CONDITIONS_SEARCH_OPTION = "usePreviousConditionsSearch"
    }

    private val usePreviousConditionsSearchOptionKey = booleanPreferencesKey(USE_PREVIOUS_CONDITIONS_SEARCH_OPTION)

    private val ConditionKeys = object {
        booleanPreferencesKey("${PREVIOUS_CONDITIONS}_{$REPO}")
    }

    override suspend fun savePreviousConditions(value: SearchConditions) {
        dataStore.edit { preferences ->
            preferences[usePreviousConditionsSearchOptionKey] = value.typeOptions.
        }
    }

    override suspend fun saveUsePreviousConditionsSearch(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[usePreviousConditionsSearchOptionKey] = value
        }
    }
}