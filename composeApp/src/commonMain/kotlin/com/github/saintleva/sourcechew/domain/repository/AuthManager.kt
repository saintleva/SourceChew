package com.github.saintleva.sourcechew.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthManager {

    val authToken: Flow<String?>
    val isAuthorized: Flow<Boolean>

    suspend fun saveToken(token: String)
    suspend fun clearToken()
}