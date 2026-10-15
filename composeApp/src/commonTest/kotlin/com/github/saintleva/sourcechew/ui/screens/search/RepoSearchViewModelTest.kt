package com.github.saintleva.sourcechew.ui.screens.search

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.BaseSearchConditions
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.Range
import com.github.saintleva.sourcechew.domain.models.RepoOnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
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

private class FakeConfigStore<T>(initial: T) : ConfigStore<T> {
    private val _config = MutableStateFlow(initial)
    override val config: Flow<T> = _config
    override suspend fun update(updater: (T) -> T) {
        _config.update(updater)
    }
}

private class FakeSearchInteractor<C : BaseSearchConditions<C>, I : FoundBase> : SearchInteractor<C, I> {
    override val searchState: StateFlow<SearchState<I>> = MutableStateFlow(SearchState.Selecting)
    override val everSearched: Boolean = false
    override var lastScrollPosition: ScrollPosition? = null
    override suspend fun search(conditions: C, usePreviousSearch: Boolean) {}
    override fun canUsePreviousConditions(newConditions: C): Boolean = false
    override fun switchToSelecting() {}
    override fun clear() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class RepoSearchViewModelTest : FunSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    test("onStarsFilterChange updates stars condition") {
        val viewModel = RepoSearchViewModel(
            conditionsStore = FakeConfigStore(RepoSearchConditions.default),
            appSettingsStore = FakeConfigStore(AppSettings.default),
            searchInteractor = FakeSearchInteractor<RepoSearchConditions, FoundRepo>()
        )
        val stars = Range.atLeast(100)
        viewModel.onStarsFilterChange(stars)
        viewModel.conditions.value.stars shouldBe stars

        viewModel.onStarsFilterChange(null)
        viewModel.conditions.value.stars shouldBe null
    }

    test("onForksFilterChange updates forks condition") {
        val viewModel = RepoSearchViewModel(
            conditionsStore = FakeConfigStore(RepoSearchConditions.default),
            appSettingsStore = FakeConfigStore(AppSettings.default),
            searchInteractor = FakeSearchInteractor<RepoSearchConditions, FoundRepo>()
        )
        val forks = Range.between(10, 50)
        viewModel.onForksFilterChange(forks)
        viewModel.conditions.value.forks shouldBe forks
    }

    test("onSizeFilterChange updates size condition") {
        val viewModel = RepoSearchViewModel(
            conditionsStore = FakeConfigStore(RepoSearchConditions.default),
            appSettingsStore = FakeConfigStore(AppSettings.default),
            searchInteractor = FakeSearchInteractor<RepoSearchConditions, FoundRepo>()
        )
        val size = Range.atMost(1024)
        viewModel.onSizeFilterChange(size)
        viewModel.conditions.value.size shouldBe size
    }

    test("onPushedFilterChange updates pushed condition") {
        val viewModel = RepoSearchViewModel(
            conditionsStore = FakeConfigStore(RepoSearchConditions.default),
            appSettingsStore = FakeConfigStore(AppSettings.default),
            searchInteractor = FakeSearchInteractor<RepoSearchConditions, FoundRepo>()
        )
        val pushed = Range.exact(LocalDate(2024, 1, 1))
        viewModel.onPushedFilterChange(pushed)
        viewModel.conditions.value.pushed shouldBe pushed
    }

    test("Set string filters update conditions") {
        val viewModel = RepoSearchViewModel(
            conditionsStore = FakeConfigStore(RepoSearchConditions.default),
            appSettingsStore = FakeConfigStore(AppSettings.default),
            searchInteractor = FakeSearchInteractor<RepoSearchConditions, FoundRepo>()
        )

        viewModel.onOwnersChange(setOf("octocat", "google"))
        viewModel.conditions.value.owners shouldBe setOf("octocat", "google")

        viewModel.onLanguagesChange(setOf("Kotlin", "Rust"))
        viewModel.conditions.value.languages shouldBe setOf("Kotlin", "Rust")

        viewModel.onTopicsChange(setOf("compose", "android"))
        viewModel.conditions.value.topics shouldBe setOf("compose", "android")

        viewModel.onLicensesChange(setOf("mit", "apache-2.0"))
        viewModel.conditions.value.licenses shouldBe setOf("mit", "apache-2.0")
    }

    test("Scope and flag toggles work properly") {
        val viewModel = RepoSearchViewModel(
            conditionsStore = FakeConfigStore(RepoSearchConditions.default),
            appSettingsStore = FakeConfigStore(AppSettings.default),
            searchInteractor = FakeSearchInteractor<RepoSearchConditions, FoundRepo>()
        )

        viewModel.toggleScope(RepoSearchScope.DESCRIPTION)
        viewModel.conditions.value.inScope shouldBe setOf(RepoSearchScope.NAME, RepoSearchScope.DESCRIPTION)

        viewModel.toggleOnlyFlag(RepoOnlyFlag.FORK)
        viewModel.conditions.value.onlyFlags shouldBe setOf(RepoOnlyFlag.FORK)

        viewModel.onSortChange(RepoSearchSort.STARS)
        viewModel.conditions.value.sort shouldBe RepoSearchSort.STARS
    }
})
