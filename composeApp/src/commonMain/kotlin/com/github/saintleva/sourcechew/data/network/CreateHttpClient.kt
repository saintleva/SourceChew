package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.repository.AuthRepository
import com.github.saintleva.sourcechew.domain.result.InvalidTokenException
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


internal fun createHttpClient(authRepository: AuthRepository, baseUrl: String) = HttpClient {
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
                val token = authRepository.getAccessToken() ?: return@loadTokens null

                // Validation: GitHub tokens must consist of printable ASCII characters.
                // If non-ASCII characters (e.g., Cyrillic) are present, throw an exception.
                val isValidAscii = token.all { it in ' '..'~' }
                if (!isValidAscii) {
                    throw InvalidTokenException()
                }

                BearerTokens(token, "")
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