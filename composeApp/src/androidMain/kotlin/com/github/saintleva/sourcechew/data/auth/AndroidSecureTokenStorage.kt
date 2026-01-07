package com.github.saintleva.sourcechew.data.auth

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first


class AndroidSecureTokenStorage(
    private val dataStore: DataStore<Preferences>
) : SecureTokenStorage {

    companion object {
        private val key = stringPreferencesKey("auth_token")
    }

    private val crypto = AndroidCryptoManager

    override suspend fun read(): String? {
        val encoded = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()[key] ?: return null
        val bytes = Base64.decode(encoded, Base64.NO_WRAP)
        return try {
            crypto.decrypt(bytes).decodeToString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun write(token: String) {
        val encrypted = crypto.encrypt(token.encodeToByteArray())
        val encoded = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        dataStore.edit { it[key] = encoded }
    }

    override suspend fun clear() {
        dataStore.edit { it.remove(key) }
    }
}