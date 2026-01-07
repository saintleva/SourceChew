package com.github.saintleva.sourcechew.di

import com.github.saintleva.sourcechew.data.auth.AuthManagerImpl
import com.github.saintleva.sourcechew.data.auth.FakeAuthManager
import com.github.saintleva.sourcechew.data.network.KtorRestApiService
import com.github.saintleva.sourcechew.domain.repository.AuthManager
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import org.koin.dsl.module


private fun createHttpClient(authManager: AuthManager, baseUrl: String) = HttpClient {
    install(ContentNegotiation) {
        json(Json{
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(Auth) {
        bearer {
            loadTokens {
                val token = authManager.authToken.firstOrNull()
                if (token != null) BearerTokens(token, "")
                else null
            }
            sendWithoutRequest { true }
        }
    }
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = baseUrl
        }
    }
}

val networkModule = module {
    single<AuthManager> { AuthManagerImpl(storage = get()) }
    single<SearchApiService> {
        KtorRestApiService(httpClient = createHttpClient(get(), baseUrl = "api.github.com"))
        //TODO: Remove this
        //MockSearchApiService(simulateErrorProbability = 0.0, returnEmptyListProbability = 0.5, eachCount = 100, delayImitation = 100.milliseconds)
    }
}