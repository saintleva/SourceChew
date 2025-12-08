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

import com.github.saintleva.sourcechew.data.paging.PagingSourceFactoryImpl
import com.github.saintleva.sourcechew.data.storage.DataStoreConfigManager
import com.github.saintleva.sourcechew.domain.repository.ConfigManager
import com.github.saintleva.sourcechew.domain.repository.PagingSourceFactory
import com.github.saintleva.sourcechew.domain.usecase.GetReposUseCase
import com.github.saintleva.sourcechew.domain.usecase.GetReposUseCaseImpl
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.RepoSearchInteractorImpl
import org.koin.dsl.module


val domainModule = module {
    single<ConfigManager> { DataStoreConfigManager(dataStore = get()) }
    single<PagingSourceFactory> { PagingSourceFactoryImpl(apiService = get()) }
    factory<GetReposUseCase> { GetReposUseCaseImpl(pagingSourceFactory = get(), configManager = get()) }
    single<RepoSearchInteractor> { RepoSearchInteractorImpl(getReposUseCase = get()) }
}
