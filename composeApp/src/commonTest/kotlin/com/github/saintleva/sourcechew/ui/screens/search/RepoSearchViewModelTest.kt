package com.github.saintleva.sourcechew.ui.screens.search

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.Range
import com.github.saintleva.sourcechew.domain.models.RepoOnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.test.FakeConfigStore
import com.github.saintleva.sourcechew.test.FakeSearchInteractor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate

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
