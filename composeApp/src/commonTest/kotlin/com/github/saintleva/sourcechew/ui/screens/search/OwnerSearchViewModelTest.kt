package com.github.saintleva.sourcechew.ui.screens.search

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.BaseSearchConditions
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.OwnerSearchScope
import com.github.saintleva.sourcechew.domain.models.OwnerSearchSort
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.models.Range
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.github.saintleva.sourcechew.domain.usecase.SearchInteractor
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate

private class FakeOwnerConfigStore<T>(initial: T) : ConfigStore<T> {
    private val _config = MutableStateFlow(initial)
    override val config: Flow<T> = _config
    override suspend fun update(updater: (T) -> T) {
        _config.update(updater)
    }
}

private class FakeOwnerSearchInteractor<C : BaseSearchConditions<C>, I : FoundBase> : SearchInteractor<C, I> {
    override val searchState: StateFlow<SearchState<I>> = MutableStateFlow(SearchState.Selecting)
    override val everSearched: Boolean = false
    override var lastScrollPosition: ScrollPosition? = null
    override suspend fun search(conditions: C, usePreviousSearch: Boolean) {}
    override fun canUsePreviousConditions(newConditions: C): Boolean = false
    override fun switchToSelecting() {}
    override fun clear() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class OwnerSearchViewModelTest : FunSpec(
    {
        val testDispatcher = StandardTestDispatcher()

        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        test("onReposFilterChange updates repos condition") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeOwnerConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeOwnerConfigStore(AppSettings.default),
                searchInteractor = FakeOwnerSearchInteractor(),
            )
            val repos = Range.atLeast(10)
            viewModel.onReposFilterChange(repos)
            viewModel.conditions.value.repos shouldBe repos

            viewModel.onReposFilterChange(null)
            viewModel.conditions.value.repos shouldBe null
        }

        test("onFollowersFilterChange updates followers condition") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeOwnerConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeOwnerConfigStore(AppSettings.default),
                searchInteractor = FakeOwnerSearchInteractor(),
            )
            val followers = Range.between(100, 500)
            viewModel.onFollowersFilterChange(followers)
            viewModel.conditions.value.followers shouldBe followers
        }

        test("onCreatedFilterChange updates created condition") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeOwnerConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeOwnerConfigStore(AppSettings.default),
                searchInteractor = FakeOwnerSearchInteractor(),
            )
            val created = Range.exact(LocalDate(2023, 5, 1))
            viewModel.onCreatedFilterChange(created)
            viewModel.conditions.value.created shouldBe created
        }

        test("onLocationsChange updates locations condition") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeOwnerConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeOwnerConfigStore(AppSettings.default),
                searchInteractor = FakeOwnerSearchInteractor(),
            )
            val locations = setOf("San Francisco", "London")
            viewModel.onLocationsChange(locations)
            viewModel.conditions.value.locations shouldBe locations
        }

        test("Scope, type and sort toggles work properly") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeOwnerConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeOwnerConfigStore(AppSettings.default),
                searchInteractor = FakeOwnerSearchInteractor(),
            )

            viewModel.toggleScope(OwnerSearchScope.FULLNAME)
            viewModel.conditions.value.inScope shouldBe setOf(OwnerSearchScope.LOGIN, OwnerSearchScope.FULLNAME)

            viewModel.toggleType(OwnerType.ORGANIZATION)
            viewModel.conditions.value.types shouldBe setOf(OwnerType.USER)

            viewModel.onSortChange(OwnerSearchSort.FOLLOWERS)
            viewModel.conditions.value.sort shouldBe OwnerSearchSort.FOLLOWERS
        }
    },
)
