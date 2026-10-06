/*
 * Copyright (C) Anton Liaukevich 2021-2022 <leva.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.saintleva.sourcechew.di

import androidx.datastore.core.okio.OkioSerializer
import com.github.saintleva.sourcechew.data.auth.AuthRepositoryImpl
import com.github.saintleva.sourcechew.data.secure.DefaultTokenStorage
import com.github.saintleva.sourcechew.data.secure.SecureTokenStorage
import com.github.saintleva.sourcechew.data.storage.AppPreferences
import com.github.saintleva.sourcechew.data.storage.BytesCodec
import com.github.saintleva.sourcechew.data.storage.CodecOkioSerializer
import com.github.saintleva.sourcechew.data.storage.DataStoreConfigStore
import com.github.saintleva.sourcechew.data.storage.StringFormatCodec
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.AuthRepository
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.FetchItemsUseCase
import com.github.saintleva.sourcechew.domain.usecase.FetchItemsUseCaseImpl
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractorImpl
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module


val domainModule = module {

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

    single<ConfigStore<AppSettings>>(qualifier<AppSettings>()) {
        DataStoreConfigStore(
            dataStore = get(), // Provided by PlatformModule
            lens = AppPreferences.AppSettingsLens
        )
    }

    single<ConfigStore<RepoSearchConditions>>(qualifier<RepoSearchConditions>()) {
        DataStoreConfigStore(
            dataStore = get(), // Provided by PlatformModule
            lens = AppPreferences.RepoSearchLens
        )
    }

    single<ConfigStore<OwnerSearchConditions>>(qualifier<OwnerSearchConditions>()) {
        DataStoreConfigStore(
            dataStore = get(), // Provided by PlatformModule
            lens = AppPreferences.OwnerSearchLens
        )
    }

    single<SecureTokenStorage> { DefaultTokenStorage(storage = get()) }

    single<AuthRepository> { AuthRepositoryImpl(storage = get()) }

    factory<FetchItemsUseCase<RepoSearchConditions, FoundRepo>>(qualifier<FoundRepo>()) {
        FetchItemsUseCaseImpl(
            appSettingsStore = get(qualifier<AppSettings>()),
            searchApiService = get(qualifier<FoundRepo>())
        )
    }

    factory<FetchItemsUseCase<OwnerSearchConditions, FoundOwner>>(qualifier<FoundOwner>()) {
        FetchItemsUseCaseImpl(
            appSettingsStore = get(qualifier<AppSettings>()),
            searchApiService = get(qualifier<FoundOwner>())
        )
    }

    single<SearchInteractor<RepoSearchConditions, FoundRepo>>(qualifier<FoundRepo>()) {
        SearchInteractorImpl(fetchItemsUseCase = get(qualifier<FoundRepo>()))
    }

    single<SearchInteractor<OwnerSearchConditions, FoundOwner>>(qualifier<FoundOwner>()) {
        SearchInteractorImpl(fetchItemsUseCase = get(qualifier<FoundOwner>()))
    }
}