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

import com.github.saintleva.sourcechew.ui.screens.auth.AuthViewModel
import com.github.saintleva.sourcechew.ui.screens.found.FoundViewModel
import com.github.saintleva.sourcechew.ui.screens.search.SearchViewModel
import com.github.saintleva.sourcechew.ui.screens.settings.SettingsViewModel
import com.mobilebytelabs.kmptoolkit.clipboard.ClipboardManager
import com.mobilebytelabs.kmptoolkit.clipboard.ClipboardManagerConfig
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single<ClipboardManager> {
        ClipboardManager(ClipboardManagerConfig(async = true) )
    }

    //TODO: Migrate to this ord.koin.plugin.module.dsl.viewModel and use this and Koin Complier Plugin !
//    viewModel<AuthViewModel>()
//    viewModel<SettingsViewModel>()
//    viewModel<SearchViewModel>()
//    viewModel<FoundViewModel>()

    viewModel<AuthViewModel> {
        AuthViewModel(
            repository = get(),
            clipboardManager = get()
        )
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(appSettingsStore = get(qualifier = AppSettingsStoreQualifier))
    }

    viewModel<SearchViewModel> {
        SearchViewModel(
            repoConditionsStore = get(qualifier = RepoSearchConditionsStoreQualifier),
            appSettingsStore = get(qualifier = AppSettingsStoreQualifier),
            searchInteractor = get(),
        )
    }

    viewModel<FoundViewModel> {
        FoundViewModel(
            searchInteractor = get()
        )
    }
}