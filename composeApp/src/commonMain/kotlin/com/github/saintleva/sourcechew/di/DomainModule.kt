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

import com.github.saintleva.sourcechew.data.auth.AuthRepositoryImpl
import com.github.saintleva.sourcechew.data.secure.DefaultTokenStorage
import com.github.saintleva.sourcechew.data.secure.SecureTokenStorage
import com.github.saintleva.sourcechew.data.storage.AppPreferences
import com.github.saintleva.sourcechew.data.storage.DataStoreConfigStore
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.AuthRepository
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.FetchBranchesUseCase
import com.github.saintleva.sourcechew.domain.usecase.FetchBranchesUseCaseImpl
import com.github.saintleva.sourcechew.domain.usecase.FetchFoundItemsUseCase
import com.github.saintleva.sourcechew.domain.usecase.FetchFoundItemsUseCaseImpl
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractorImpl
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module


val domainModule = module {

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

    factory<FetchFoundItemsUseCase<RepoSearchConditions, FoundRepo>>(qualifier<FoundRepo>()) {
        FetchFoundItemsUseCaseImpl(
            appSettingsStore = get(qualifier<AppSettings>()),
            searchApiService = get(qualifier<FoundRepo>())
        )
    }

    factory<FetchFoundItemsUseCase<OwnerSearchConditions, FoundOwner>>(qualifier<FoundOwner>()) {
        FetchFoundItemsUseCaseImpl(
            appSettingsStore = get(qualifier<AppSettings>()),
            searchApiService = get(qualifier<FoundOwner>())
        )
    }

    factory<FetchBranchesUseCase> {
        FetchBranchesUseCaseImpl(
            appSettingsStore = get(qualifier<AppSettings>()),
            branchesApiService = get()
        )
    }

    single<SearchInteractor<RepoSearchConditions, FoundRepo>>(qualifier<FoundRepo>()) {
        SearchInteractorImpl(fetchFoundItemsUseCase = get(qualifier<FoundRepo>()))
    }

    single<SearchInteractor<OwnerSearchConditions, FoundOwner>>(qualifier<FoundOwner>()) {
        SearchInteractorImpl(fetchFoundItemsUseCase = get(qualifier<FoundOwner>()))
    }
}