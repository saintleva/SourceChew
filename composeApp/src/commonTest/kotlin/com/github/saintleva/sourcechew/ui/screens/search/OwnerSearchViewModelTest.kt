package com.github.saintleva.sourcechew.ui.screens.search

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.OwnerSearchScope
import com.github.saintleva.sourcechew.domain.models.OwnerSearchSort
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.models.Range
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
                conditionsStore = FakeConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeConfigStore(AppSettings.default),
                searchInteractor = FakeSearchInteractor(),
            )
            val repos = Range.atLeast(10)
            viewModel.onReposFilterChange(repos)
            viewModel.conditions.value.repos shouldBe repos

            viewModel.onReposFilterChange(null)
            viewModel.conditions.value.repos shouldBe null
        }

        test("onFollowersFilterChange updates followers condition") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeConfigStore(AppSettings.default),
                searchInteractor = FakeSearchInteractor(),
            )
            val followers = Range.between(100, 500)
            viewModel.onFollowersFilterChange(followers)
            viewModel.conditions.value.followers shouldBe followers
        }

        test("onCreatedFilterChange updates created condition") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeConfigStore(AppSettings.default),
                searchInteractor = FakeSearchInteractor(),
            )
            val created = Range.exact(LocalDate(2023, 5, 1))
            viewModel.onCreatedFilterChange(created)
            viewModel.conditions.value.created shouldBe created
        }

        test("onLocationsChange updates locations condition") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeConfigStore(AppSettings.default),
                searchInteractor = FakeSearchInteractor(),
            )
            val locations = setOf("San Francisco", "London")
            viewModel.onLocationsChange(locations)
            viewModel.conditions.value.locations shouldBe locations
        }

        test("Scope, type and sort toggles work properly") {
            val viewModel = OwnerSearchViewModel(
                conditionsStore = FakeConfigStore(OwnerSearchConditions.default),
                appSettingsStore = FakeConfigStore(AppSettings.default),
                searchInteractor = FakeSearchInteractor(),
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
