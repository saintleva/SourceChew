package com.github.saintleva.sourcechew.di

import com.github.saintleva.sourcechew.data.network.KtorRestApiService
import com.github.saintleva.sourcechew.data.network.createHttpClient
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import org.koin.dsl.module


val dataModule = module {
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