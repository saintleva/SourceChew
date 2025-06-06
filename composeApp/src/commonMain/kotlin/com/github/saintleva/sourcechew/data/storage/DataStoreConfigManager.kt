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
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.saintleva.sourcechew.data.utils.searchQueryToString
import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.models.TypeOptions
import com.github.saintleva.sourcechew.ui.common.utils.parseSearchQuery
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


class DataStoreConfigManager(private val dataStore: DataStore<Preferences>) : ConfigManager {

    private companion object {

        object PreviousConditionsKeys {

            private const val PREVIOUS_CONDITIONS = "PreviousConditions"

            val forgeOptions = Forge.list.associateWith {
                booleanPreferencesKey("${PREVIOUS_CONDITIONS}_forge_${it.name}")
            }

            object TypeOptions {
                val repo = booleanPreferencesKey("${PREVIOUS_CONDITIONS}_repo")
                val user = booleanPreferencesKey("${PREVIOUS_CONDITIONS}_user")
                val group = booleanPreferencesKey("${PREVIOUS_CONDITIONS}_group")
            }

            val query = stringPreferencesKey("${PREVIOUS_CONDITIONS}_query")
        }

        val usePreviousSearchKey = booleanPreferencesKey("usePreviousSearch")
    }

    override suspend fun savePreviousConditions(value: SearchConditions) {
        dataStore.edit { preferences ->
            preferences[PreviousConditionsKeys.TypeOptions.repo] = value.typeOptions.repo
            preferences[PreviousConditionsKeys.TypeOptions.user] = value.typeOptions.user
            preferences[PreviousConditionsKeys.TypeOptions.group] = value.typeOptions.group
            PreviousConditionsKeys.forgeOptions.forEach { entry ->
                preferences[entry.value] = value.forgeOptions[entry.key]!!
            }
            preferences[PreviousConditionsKeys.query] = searchQueryToString(value.query)
        }
    }

    override suspend fun loadPreviousConditions(): SearchConditions {
        Napier.d(tag = "DataStoreConfigManager") { "loadPreviousConditions() started" }
        val typeOptions = TypeOptions(
            repo = dataStore.data.map {
                preferences -> preferences[PreviousConditionsKeys.TypeOptions.repo] ?: true
            }.first(),
            user = dataStore.data.map {
                    preferences -> preferences[PreviousConditionsKeys.TypeOptions.user] ?: false
            }.first(),
            group = dataStore.data.map {
                    preferences -> preferences[PreviousConditionsKeys.TypeOptions.group] ?: false
            }.first()
        )
        val forgeOptions = Forge.list.associateWith {
            dataStore.data.map { preferences ->
                preferences[PreviousConditionsKeys.forgeOptions[it]!!] ?: false }.first()
        }
        val queryStr = dataStore.data.map { preferences ->
            preferences[PreviousConditionsKeys.query] ?: "" }.first()
        return SearchConditions(forgeOptions, typeOptions, parseSearchQuery(queryStr))
    }


    override suspend fun saveUsePreviousSearch(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[usePreviousSearchKey] = value
        }
    }

    override suspend fun loadUsePreviousSearch(): Boolean =
        dataStore.data.map { preferences -> preferences[usePreviousSearchKey] ?: false }.first()
}