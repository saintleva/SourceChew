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

import com.github.saintleva.sourcechew.data.repository.SearchRepositoryStub
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.repository.SearchRepository
import com.github.saintleva.sourcechew.domain.usecase.CanUsePreviousConditionsUseCase
import com.github.saintleva.sourcechew.domain.usecase.CanUsePreviousConditionsUseCaseImpl
import com.github.saintleva.sourcechew.domain.usecase.FindUseCase
import com.github.saintleva.sourcechew.domain.usecase.FindUseCaseImpl
import org.koin.dsl.module
import kotlin.time.Duration.Companion.milliseconds


val domainModule = module {
    single<ConfigRepository> { ConfigRepositoryImpl(get()) }
    single<SearchRepository> { SearchRepositoryStub(10, 200.milliseconds) }
    factory<CanUsePreviousConditionsUseCase> { CanUsePreviousConditionsUseCaseImpl(get(), get()) }
    factory<FindUseCase> { FindUseCaseImpl(get(), get()) }
}
