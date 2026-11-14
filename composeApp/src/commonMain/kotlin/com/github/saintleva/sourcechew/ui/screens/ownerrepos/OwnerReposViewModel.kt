package com.github.saintleva.sourcechew.ui.screens.ownerrepos

import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.FetchOwnerReposUseCase
import com.github.saintleva.sourcechew.ui.screens.PaginatedListViewModel
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OwnerReposViewModel(
    val owner: String,
    fetchOwnerReposUseCase: FetchOwnerReposUseCase,
    appSettingsStore: ConfigStore<AppSettings>,
) : PaginatedListViewModel<String, FoundRepo>(
    key = owner,
    fetchPaginator = fetchOwnerReposUseCase::invoke,
) {
    val maxDescriptionLength: StateFlow<Int> = appSettingsStore.config
        .map { it.maxFoundRepoDescriptionLength }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = AppSettings.default.maxFoundRepoDescriptionLength,
        )
}
