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
import com.github.saintleva.sourcechew.data.utils.makeSet
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class DataStoreConfigManager(private val dataStore: DataStore<Preferences>) : ConfigManager {

    private companion object {

        abstract class Group(val type: String) {
            protected val previousConditions = "${type}_PreviousConditions"
            val queryKey = stringPreferencesKey("${previousConditions}_query")
            val usePreviousSearchKey = booleanPreferencesKey("${type}_usePreviousSearch")
        }

        object Repo : Group("Repo") {

            object PreviousConditions {
                val scopeKeys = RepoSearchScope.all.associateWith {
                    booleanPreferencesKey("${previousConditions}_scope_${it.name}")
                }
                val onlyFlagKeys = OnlyFlag.all.associateWith {
                    booleanPreferencesKey("${previousConditions}_onlyFlag_${it.name}")
                }
            }

        }
    }

    override suspend fun saveRepoPreviousConditions(value: RepoSearchConditions) {
        dataStore.edit { preferences ->
            preferences[Repo.queryKey] = value.query
            Repo.PreviousConditions.scopeKeys.forEach { entry ->
                preferences[entry.value] = entry.key in value.inScope
            }
            Repo.PreviousConditions.onlyFlagKeys.forEach { entry ->
                preferences[entry.value] = entry.key in value.onlyFlags
            }
        }
    }

    override suspend fun loadRepoPreviousConditions(): RepoSearchConditions {
        //TODO: remove this
        Napier.d(tag = "DataStoreConfigManager") { "loadPreviousConditions() started" }

        //TODO: Use good default values
        val query = dataStore.data.map { preferences -> preferences[Repo.queryKey] ?: "" }.first()
        Napier.d(tag = "DataStoreConfigManager") { "query = '$query' red" }
        val inScope = RepoSearchScope.all.makeSet {
            dataStore.data.map { preferences ->
                preferences[Repo.PreviousConditions.scopeKeys[it]!!] ?: it == RepoSearchScope.NAME
            }.first()
        }
        val onlyFlags = OnlyFlag.all.makeSet {
            dataStore.data.map { preferences ->
                preferences[Repo.PreviousConditions.onlyFlagKeys[it]!!] ?: false
            }.first()
        }

        Napier.d(tag = "DataStoreConfigManager") { "loadPreviousConditions() finished" }

        return RepoSearchConditions(query, inScope, onlyFlags)
    }


    override suspend fun saveUsePreviousRepoSearch(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Repo.usePreviousSearchKey] = value
        }
    }

    override suspend fun loadUsePreviousRepoSearch(): Boolean =
        dataStore.data.map { preferences -> preferences[Repo.usePreviousSearchKey] ?: false }.first()
}