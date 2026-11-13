package com.github.saintleva.sourcechew.ui.screens.userorgs

import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.usecase.FetchUserOrgsUseCase
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
class UserOrgsViewModelTest : FunSpec({
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    test("initialization loads paginator for user organizations") {
        var requestedUsername: String? = null
        val dummyPaginator = paginator<Owner>(capacity = 20) {
            load {
                LoadResult(data = emptyList<Owner>())
            }
        }
        val fakeUseCase = object : FetchUserOrgsUseCase {
            override suspend fun invoke(username: String): Paginator<Owner> {
                requestedUsername = username
                return dummyPaginator
            }
        }

        val viewModel = UserOrgsViewModel(
            username = "test-user",
            fetchUserOrgsUseCase = fakeUseCase,
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.username shouldBe "test-user"
        requestedUsername shouldBe "test-user"
        viewModel.paginator shouldBe dummyPaginator
    }

    test("saveScroll and consumeInitialScroll maintain scroll position") {
        val fakeUseCase = object : FetchUserOrgsUseCase {
            override suspend fun invoke(username: String): Paginator<Owner> {
                return paginator(capacity = 20) {
                    load {
                        LoadResult(data = emptyList<Owner>())
                    }
                }
            }
        }

        val viewModel = UserOrgsViewModel(
            username = "test-user",
            fetchUserOrgsUseCase = fakeUseCase,
        )

        viewModel.consumeInitialScroll() shouldBe null

        viewModel.saveScroll(index = 5, offset = 100)

        val position = viewModel.consumeInitialScroll()
        position shouldNotBe null
        position?.index shouldBe 5
        position?.offset shouldBe 100

        viewModel.consumeInitialScroll() shouldBe null
    }

    test("restart reloads paginator content") {
        var loadCount = 0
        val fakeUseCase = object : FetchUserOrgsUseCase {
            override suspend fun invoke(username: String): Paginator<Owner> {
                return paginator(capacity = 20) {
                    load {
                        loadCount++
                        LoadResult(data = emptyList<Owner>())
                    }
                }
            }
        }

        val viewModel = UserOrgsViewModel(
            username = "test-user",
            fetchUserOrgsUseCase = fakeUseCase,
        )

        testDispatcher.scheduler.advanceUntilIdle()
        val initialLoadCount = loadCount

        viewModel.restart()
        testDispatcher.scheduler.advanceUntilIdle()

        loadCount shouldBe (initialLoadCount + 1)
    }
})
