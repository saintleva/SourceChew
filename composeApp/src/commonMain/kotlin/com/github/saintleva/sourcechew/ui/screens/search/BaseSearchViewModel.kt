package com.github.saintleva.sourcechew.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.BaseSearchConditions
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.models.updateCommonFilters
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.ui.utils.DEBOUNCE
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
abstract class BaseSearchViewModel<SearchConditions : BaseSearchConditions<SearchConditions>, FoundItem : FoundBase>(
    private val conditionsStore: ConfigStore<SearchConditions>,
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val searchInteractor: SearchInteractor<SearchConditions, FoundItem>,
    initialConditions: SearchConditions
) : ViewModel() {

    protected val _conditions = MutableStateFlow(initialConditions)
    val conditions: StateFlow<SearchConditions> = _conditions.asStateFlow()

    val maySearch: StateFlow<Boolean> = conditions
        .map { it.maySearch() }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = false
        )

    abstract val usePreviousSearch: StateFlow<Boolean>
    abstract fun usePreviousSearchChange(checked: Boolean)

    val searchState = searchInteractor.searchState

    private var _searchJob: Job? = null

    init {
        viewModelScope.launch {
            _conditions.value = conditionsStore.config.first()
            _conditions
                .drop(1)
                .debounce(DEBOUNCE)
                .collect { currentConditions ->
                    conditionsStore.update { currentConditions }
                }
        }
    }

    fun onQueryChange(query: String) {
        _conditions.updateCommonFilters { it.copy(query = query) }
    }

    fun onOrderChange(order: SearchOrder) {
        _conditions.updateCommonFilters { it.copy(order = order) }
    }

    fun canUsePreviousConditions() =
        searchInteractor.canUsePreviousConditions(conditions.value)

    fun search() {
        _searchJob = viewModelScope.launch {
            searchInteractor.search(conditions.value, usePreviousSearch.value)
        }
    }

    fun stop() {
        _searchJob?.cancel()
        searchInteractor.switchToSelecting()
    }
}