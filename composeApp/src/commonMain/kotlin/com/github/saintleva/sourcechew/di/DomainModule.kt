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
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.AuthRepository
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.GetReposUseCase
import com.github.saintleva.sourcechew.domain.usecase.GetReposUseCaseImpl
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractorImpl
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module


object ConfigJsonQualifier : Qualifier {
    override val value: QualifierValue = "com.github.saintleva.sourcechew.di.ConfigJsonQualifier"
}

object AppSettingsStoreQualifier : Qualifier {
    override val value: QualifierValue = "com.github.saintleva.sourcechew.di.AppSettingsStoreQualifier"
}

object RepoSearchConditionsStoreQualifier : Qualifier {
    override val value: QualifierValue = "com.github.saintleva.sourcechew.di.RepoSearchConditionsStoreQualifier"
}

val domainModule = module {

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

    single<ConfigStore<AppSettings>>(qualifier = AppSettingsStoreQualifier) {
        DataStoreConfigStore(
            dataStore = get(), // Provided by PlatformModule
            lens = AppPreferences.AppSettingsLens
        )
    }

    single<ConfigStore<RepoSearchConditions>>(qualifier = RepoSearchConditionsStoreQualifier) {
        DataStoreConfigStore(
            dataStore = get(), // Provided by PlatformModule
            lens = AppPreferences.RepoSearchLens
        )
    }

    single<SecureTokenStorage> { DefaultTokenStorage(storage = get()) }

    single<AuthRepository> { AuthRepositoryImpl(storage = get()) }

    factory<GetReposUseCase> {
        GetReposUseCaseImpl(
            appSettingsStore = get(qualifier = AppSettingsStoreQualifier),
            searchApiService = get()
        )
    }

    single<RepoSearchInteractor> { RepoSearchInteractorImpl(getReposUseCase = get()) }
}