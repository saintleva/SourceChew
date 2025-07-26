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
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditionsFlow
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class DataStoreConfigManager(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO //TODO: Do I really need this?
) : ConfigManager {

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

    suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun <T> read(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data.map { preferences -> preferences[key] ?: defaultValue }

    override val previousRepoConditions = RepoSearchConditionsFlow(
        query = read(Repo.queryKey, ""),
        inScope = RepoSearchScope.all.associateWith {
            read(Repo.PreviousConditions.scopeKeys[it]!!,
                if (it == RepoSearchScope.NAME) true else false)
        },
        onlyFlags = OnlyFlag.all.associateWith {
            read(Repo.PreviousConditions.onlyFlagKeys[it]!!, false)
        },
        usePreviousSearch = read(Repo.usePreviousSearchKey, false)
    )

    override val repoSearchConditionsSaver = object : ConfigManager.RepoSearchConditionsSaver {

        override suspend fun saveQuery(query: String) {
            save(Repo.queryKey, query)
        }

        override suspend fun saveScopeItem(item: RepoSearchScope) {
            save(Repo.PreviousConditions.scopeKeys[item]!!, true)
        }

        override suspend fun saveOnlyFlag(onlyFlag: OnlyFlag) {
            save(Repo.PreviousConditions.onlyFlagKeys[onlyFlag]!!, true)
        }

        override suspend fun saveUsePreviousSearch(value: Boolean) {
            save(Repo.usePreviousSearchKey, value)
        }
    }
}