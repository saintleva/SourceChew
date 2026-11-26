package com.github.saintleva.sourcechew.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.BaseSearchConditions
import com.github.saintleva.sourcechew.domain.models.Range
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.models.updateCommonFilters
import kotlinx.datetime.LocalDate
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import com.github.saintleva.sourcechew.domain.utils.Lens
import com.github.saintleva.sourcechew.ui.utils.DEBOUNCE
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SearchNavEvent {
    data object NavigateToResults : SearchNavEvent
}


@OptIn(FlowPreview::class)
abstract class BaseSearchViewModel<SearchConditions : BaseSearchConditions<SearchConditions>, FoundItem : FoundBase>(
    private val conditionsStore: ConfigStore<SearchConditions>,
    private val appSettingsStore: ConfigStore<AppSettings>,
    private val searchInteractor: SearchInteractor<SearchConditions, FoundItem>,
    protected val initialConditions: SearchConditions,
    private val usePreviousSearchLens: Lens<AppSettings, Boolean>
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

    val usePreviousSearch: StateFlow<Boolean> = appSettingsStore.config
        .map { usePreviousSearchLens.get(it) }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = usePreviousSearchLens.get(AppSettings.default)
        )

    fun usePreviousSearchChange(checked: Boolean) {
        if (!checked) {
            searchInteractor.clear()
        }
        viewModelScope.launch {
            appSettingsStore.update { usePreviousSearchLens.set(it, checked) }
        }
    }

    val searchState = searchInteractor.searchState

    private val _navEvents = Channel<SearchNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<SearchNavEvent> = _navEvents.receiveAsFlow()

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
        if (query.isBlank()) {
            searchInteractor.clear()
        }
    }

    fun clearSearch() {
        _conditions.updateCommonFilters { it.copy(query = "") }
        searchInteractor.clear()
    }

    open fun resetFilters() {
        _conditions.value = initialConditions
        searchInteractor.clear()
    }

    fun onCreatedFilterChange(created: Range<LocalDate>?) {
        _conditions.updateCommonFilters { it.copy(created = created) }
    }

    fun onOrderChange(order: SearchOrder) {
        _conditions.updateCommonFilters { it.copy(order = order) }
    }

    fun canUsePreviousConditions() =
        searchInteractor.canUsePreviousConditions(conditions.value)

    fun search() {
        _searchJob?.cancel()
        _searchJob = viewModelScope.launch {
            searchInteractor.search(conditions.value, usePreviousSearch.value)
            if (searchInteractor.searchState.value is SearchState.Found) {
                _navEvents.send(SearchNavEvent.NavigateToResults)
            }
        }
    }

    fun stop() {
        _searchJob?.cancel()
        searchInteractor.switchToSelecting()
    }
}