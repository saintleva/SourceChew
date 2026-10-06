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

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.ui.screens.auth.AuthViewModel
import com.github.saintleva.sourcechew.ui.screens.found.FoundViewModel
import com.github.saintleva.sourcechew.ui.screens.search.OwnerSearchViewModel
import com.github.saintleva.sourcechew.ui.screens.search.RepoSearchViewModel
import com.github.saintleva.sourcechew.ui.screens.settings.SettingsViewModel
import com.mobilebytelabs.kmptoolkit.clipboard.ClipboardManager
import com.mobilebytelabs.kmptoolkit.clipboard.ClipboardManagerConfig
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module


val appModule = module {

    single<ClipboardManager> {
        ClipboardManager(ClipboardManagerConfig(async = true) )
    }

    viewModel<AuthViewModel> {
        AuthViewModel(
            repository = get(),
            clipboardManager = get()
        )
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(appSettingsStore = get(qualifier<AppSettings>()))
    }

    viewModel<RepoSearchViewModel> {
        RepoSearchViewModel(
            conditionsStore = get(qualifier<RepoSearchConditions>()),
            appSettingsStore = get(qualifier<AppSettings>()),
            searchInteractor = get(qualifier<FoundRepo>()),
        )
    }

    viewModel<OwnerSearchViewModel> {
        OwnerSearchViewModel(
            conditionsStore = get(qualifier<OwnerSearchConditions>()),
            appSettingsStore = get(qualifier<AppSettings>()),
            searchInteractor = get(qualifier<FoundOwner>()),
        )
    }

    viewModel<FoundViewModel<RepoSearchConditions, FoundRepo>>(qualifier<FoundRepo>()) {
        FoundViewModel(
            searchInteractor = get(qualifier<FoundRepo>())
        )
    }

    viewModel<FoundViewModel<OwnerSearchConditions, FoundOwner>>(qualifier<FoundOwner>()) {
        FoundViewModel(
            searchInteractor = get(qualifier<FoundOwner>())
        )
    }
}