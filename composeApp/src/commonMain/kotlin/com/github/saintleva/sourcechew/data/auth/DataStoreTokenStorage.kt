package com.github.saintleva.sourcechew.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first


class DataStoreTokenStorage(
    private val dataStore: DataStore<Preferences>
) : SecureTokenStorage {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    override suspend fun read(): String? {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()[TOKEN_KEY]
    }

    override suspend fun write(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }
}