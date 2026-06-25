package com.github.saintleva.sourcechew.di

import androidx.datastore.core.okio.OkioSerializer
import com.github.saintleva.sourcechew.data.network.KtorRestApiService
import com.github.saintleva.sourcechew.data.network.createHttpClient
import com.github.saintleva.sourcechew.data.storage.AppPreferences
import com.github.saintleva.sourcechew.data.storage.BytesCodec
import com.github.saintleva.sourcechew.data.storage.CodecOkioSerializer
import com.github.saintleva.sourcechew.data.storage.StringFormatCodec
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import org.koin.dsl.module


val dataModule = module {

    single<StringFormat>(qualifier = ConfigJsonQualifier) {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    single<BytesCodec<AppPreferences>> {
        StringFormatCodec(
            format = get(qualifier = ConfigJsonQualifier),
            serializer = AppPreferences.serializer()
        )
    }

    single<OkioSerializer<AppPreferences>> {
        CodecOkioSerializer(
            defaultValue = AppPreferences(),
            codec = get()
        )
    }

    single<SearchApiService> {
        KtorRestApiService(
            httpClient = createHttpClient(
                authRepository = get(),
                baseUrl = "api.github.com"
            )
        )
        //TODO: Remove this
        //MockSearchApiService(simulateErrorProbability = 0.0, returnEmptyListProbability = 0.5, eachCount = 100, delayImitation = 100.milliseconds)
    }
}