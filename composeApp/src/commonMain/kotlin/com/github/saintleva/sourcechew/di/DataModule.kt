package com.github.saintleva.sourcechew.di

import androidx.datastore.core.okio.OkioSerializer
import com.github.saintleva.sourcechew.data.network.KtorOwnerDetailsApiService
import com.github.saintleva.sourcechew.data.network.KtorSearchInOwnersApiService
import com.github.saintleva.sourcechew.data.network.KtorRepoDetailsApiService
import com.github.saintleva.sourcechew.data.network.KtorSearchInReposApiService
import com.github.saintleva.sourcechew.domain.repository.OwnerDetailsApiService
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsApiService
import com.github.saintleva.sourcechew.data.network.createHttpClient
import com.github.saintleva.sourcechew.data.repository.OwnerDetailsRepositoryImpl
import com.github.saintleva.sourcechew.data.repository.RepoDetailsRepositoryImpl
import com.github.saintleva.sourcechew.data.storage.AppPreferences
import com.github.saintleva.sourcechew.data.storage.BytesCodec
import com.github.saintleva.sourcechew.data.storage.CodecOkioSerializer
import com.github.saintleva.sourcechew.data.storage.StringFormatCodec
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.OwnerDetailsRepository
import com.github.saintleva.sourcechew.domain.repository.RepoDetailsRepository
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import io.ktor.client.HttpClient
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module


val dataModule = module {

    single<StringFormat>(qualifier<AppPreferences>()) {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    single<BytesCodec<AppPreferences>> {
        StringFormatCodec(
            format = get(qualifier<AppPreferences>()),
            serializer = AppPreferences.serializer()
        )
    }

    single<OkioSerializer<AppPreferences>> {
        CodecOkioSerializer(
            defaultValue = AppPreferences(),
            codec = get()
        )
    }

    single<HttpClient> {
        createHttpClient(
            authRepository = get(),
            baseUrl = "api.github.com"
        )
    }

    single<SearchApiService<RepoSearchConditions, FoundRepo>>(qualifier<FoundRepo>()) {
        KtorSearchInReposApiService(httpClient = get())
    }

    single<SearchApiService<OwnerSearchConditions, FoundOwner>>(qualifier<FoundOwner>()) {
        KtorSearchInOwnersApiService(httpClient = get())
    }

    single<RepoDetailsApiService> {
        KtorRepoDetailsApiService(httpClient = get())
    }

    single<OwnerDetailsApiService> {
        KtorOwnerDetailsApiService(httpClient = get())
    }

    single<RepoDetailsRepository> {
        RepoDetailsRepositoryImpl(apiService = get())
    }

    single<OwnerDetailsRepository> {
        OwnerDetailsRepositoryImpl(apiService = get())
    }
}