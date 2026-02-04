package com.github.saintleva.sourcechew.data.secure

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

class DataStoreKeyValueStorage(
    private val dataStore: DataStore<Preferences>
) : SecureKeyValueStorage {

    override suspend fun read(key: String): String? {
        val preferencesKey = stringPreferencesKey(key)
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()[preferencesKey]
    }

    override suspend fun write(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun remove(key: String) {
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit { it.remove(preferencesKey) }
    }
}