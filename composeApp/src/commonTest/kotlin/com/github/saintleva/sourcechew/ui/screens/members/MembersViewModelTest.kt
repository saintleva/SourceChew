package com.github.saintleva.sourcechew.ui.screens.members

import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.usecase.FetchMembersUseCase
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.dsl.paginator
import com.jamal_aliev.paginator.offset.load.LoadResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MembersViewModelTest : FunSpec({
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    test("initialization loads paginator for organization") {
        var requestedOrg: String? = null
        val dummyPaginator = paginator<Owner>(capacity = 20) {
            load {
                LoadResult(data = emptyList<Owner>())
            }
        }
        val fakeUseCase = object : FetchMembersUseCase {
            override suspend fun invoke(org: String): Paginator<Owner> {
                requestedOrg = org
                return dummyPaginator
            }
        }

        val viewModel = MembersViewModel(
            org = "test-org",
            fetchMembersUseCase = fakeUseCase,
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.org shouldBe "test-org"
        requestedOrg shouldBe "test-org"
        viewModel.paginator shouldBe dummyPaginator
    }

    test("saveScroll and consumeInitialScroll maintain scroll position") {
        val fakeUseCase = object : FetchMembersUseCase {
            override suspend fun invoke(org: String): Paginator<Owner> {
                return paginator(capacity = 20) {
                    load {
                        LoadResult(data = emptyList<Owner>())
                    }
                }
            }
        }

        val viewModel = MembersViewModel(
            org = "test-org",
            fetchMembersUseCase = fakeUseCase,
        )

        viewModel.consumeInitialScroll() shouldBe null

        viewModel.saveScroll(index = 5, offset = 100)

        val position = viewModel.consumeInitialScroll()
        position shouldNotBe null
        position?.index shouldBe 5
        position?.offset shouldBe 100

        viewModel.consumeInitialScroll() shouldBe null
    }

    test("refresh reloads paginator") {
        var callCount = 0
        val fakeUseCase = object : FetchMembersUseCase {
            override suspend fun invoke(org: String): Paginator<Owner> {
                callCount++
                return paginator(capacity = 20) {
                    load {
                        LoadResult(data = emptyList<Owner>())
                    }
                }
            }
        }

        val viewModel = MembersViewModel(
            org = "test-org",
            fetchMembersUseCase = fakeUseCase,
        )

        testDispatcher.scheduler.advanceUntilIdle()
        val initialCallCount = callCount

        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        callCount shouldBe (initialCallCount + 1)
    }
})
