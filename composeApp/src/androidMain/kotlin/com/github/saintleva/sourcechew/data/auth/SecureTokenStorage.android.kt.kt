package com.github.saintleva.sourcechew.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.security.KeyStore


actual class SecureTokenStorage(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val key = stringPreferencesKey("auth_token")
    }

    private val crypto = AndroidCryptoManager


}