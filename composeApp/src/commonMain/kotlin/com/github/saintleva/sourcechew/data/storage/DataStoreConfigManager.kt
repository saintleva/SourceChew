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
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.saintleva.sourcechew.domain.models.OnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditionsFlows
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class DataStoreConfigManager(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO //TODO: Do I really need this?
) : ConfigManager {

    private companion object {

        abstract class Group(type: String) {
            protected val conditions = "${type}_Conditions"
            val queryKey = stringPreferencesKey("${conditions}_query")
            val usePreviousSearchKey = booleanPreferencesKey("${type}_usePreviousSearch")
        }

        object Repo : Group("Repo") {

            object Conditions {
                val scopeKeys = RepoSearchScope.entries.associateWith {
                    booleanPreferencesKey("${conditions}_scope_${it.name}")
                }
                val onlyFlagKeys = OnlyFlag.entries.associateWith {
                    booleanPreferencesKey("${conditions}_onlyFlag_${it.name}")
                }
                val sortKey = intPreferencesKey("${conditions}_sort")
            }

        }

        object App {
            val searchOrderKey = intPreferencesKey("searchOrder")
        }
    }

    suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        //TODO: Do I really need withContext() ?
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun <T> read(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data.map { preferences -> preferences[key] ?: defaultValue }

    suspend fun toggle(key: Preferences.Key<Boolean>, defaultPrevious: Boolean) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[key] = !(preferences[key] ?: defaultPrevious)
            }
        }
    }

    override val repoConditions = object : ConfigManager.RepoSearchConditionsAccessor {

        private fun loadQueryFirstTime(): String {
            var loadedQuery: String = RepoSearchConditions.default.query
            runBlocking {
                loadedQuery = read(Repo.queryKey, RepoSearchConditions.default.query).first()
            }
            return loadedQuery
        }

        private val _queryStateFlow: MutableStateFlow<String> = MutableStateFlow(loadQueryFirstTime())

        override val flows = RepoSearchConditionsFlows(
            query = _queryStateFlow,
            //TODO: Remove this
            //query = read(Repo.queryKey, RepoSearchConditions.default.query),
            inScope = RepoSearchScope.entries.associateWith {
                read(
                    Repo.Conditions.scopeKeys[it]!!,
                    it in RepoSearchConditions.default.inScope
                )
            },
            onlyFlags = OnlyFlag.entries.associateWith {
                read(
                    Repo.Conditions.onlyFlagKeys[it]!!,
                    it in RepoSearchConditions.default.onlyFlags
                )
            },
            sort = dataStore.data.map { preferences ->
                RepoSearchSort.fromCode(preferences[Repo.Conditions.sortKey] ?: RepoSearchSort.default.code)
            },
            usePreviousSearch = read(Repo.usePreviousSearchKey, false)
        )
        override suspend fun changeQuery(query: String) {
            _queryStateFlow.update { query }
            save(Repo.queryKey, query)
        }

        override suspend fun toggleScopeItem(item: RepoSearchScope) {
            toggle(Repo.Conditions.scopeKeys[item]!!,
                item in RepoSearchConditions.default.inScope)
        }

        override suspend fun toggleOnlyFlag(onlyFlag: OnlyFlag) {
            toggle(Repo.Conditions.onlyFlagKeys[onlyFlag]!!,
                onlyFlag in RepoSearchConditions.default.onlyFlags)
        }

        override suspend fun togglePublicOnlyFlag() {
            val publicKey = Repo.Conditions.onlyFlagKeys[OnlyFlag.PUBLIC]!!
            val privateKey = Repo.Conditions.onlyFlagKeys[OnlyFlag.PRIVATE]!!
            withContext(ioDispatcher) {
                dataStore.edit { preferences ->
                    val newPublic =
                        !(preferences[publicKey] ?: (OnlyFlag.PUBLIC in RepoSearchConditions.default.onlyFlags))
                    preferences[publicKey] = newPublic
                    if (newPublic) {
                        preferences[privateKey] = false
                    }
                }
            }
        }

        override suspend fun togglePrivateOnlyFlag() {
            val publicKey = Repo.Conditions.onlyFlagKeys[OnlyFlag.PUBLIC]!!
            val privateKey = Repo.Conditions.onlyFlagKeys[OnlyFlag.PRIVATE]!!
            withContext(ioDispatcher) {
                dataStore.edit { preferences ->
                    val newPrivate =
                        !(preferences[privateKey] ?: (OnlyFlag.PRIVATE in RepoSearchConditions.default.onlyFlags))
                    preferences[privateKey] = newPrivate
                    if (newPrivate) {
                        preferences[publicKey] = false
                    }
                }
            }
        }

        override suspend fun changeSort(sort: RepoSearchSort) {
            save(Repo.Conditions.sortKey, sort.code)
        }

        override suspend fun changeUsePreviousSearch(value: Boolean) {
            save(Repo.usePreviousSearchKey, value)
        }
    }

    override val appSettings = object : ConfigManager.AppSettingsAccessor {

        override val searchOrder = dataStore.data.map { preferences ->
            SearchOrder.fromCode(preferences[App.searchOrderKey] ?: SearchOrder.default.code)
        }

        override suspend fun changeSearchOrder(order: SearchOrder) {
            save(App.searchOrderKey, order.code)
        }
    }
}