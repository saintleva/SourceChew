package com.github.saintleva.sourcechew.ui.screens.branches

import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.Commit
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.usecase.BranchData
import com.github.saintleva.sourcechew.domain.usecase.BranchDetailsResult
import com.github.saintleva.sourcechew.domain.usecase.FetchBranchesUseCase
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
class BranchesViewModelTest : FunSpec({
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    fun createDummyBranchData(defaultBranchName: String): BranchData {
        val dummyBranch = Branch(name = defaultBranchName, commit = Commit("sha-123"))
        val dummyPaginator = paginator<Branch>(capacity = 20) {
            load {
                LoadResult(data = listOf(dummyBranch))
            }
        }
        return BranchData(
            default = BranchDetailsResult.Success(dummyBranch),
            paginator = dummyPaginator
        )
    }

    test("initialization loads branch data for repoRef") {
        var requestedRepoRef: RepoRef? = null
        val dummyData = createDummyBranchData("main")

        val fakeUseCase = object : FetchBranchesUseCase {
            override suspend fun invoke(repoRef: RepoRef): BranchData {
                requestedRepoRef = repoRef
                return dummyData
            }
        }

        val repoRef = RepoRef("owner", "repo")
        val viewModel = BranchesViewModel(
            repoRef = repoRef,
            fetchBranchesUseCase = fakeUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.repoRef shouldBe repoRef
        requestedRepoRef shouldBe repoRef
        viewModel.branches.value shouldBe dummyData
        viewModel.defaultBranch?.name shouldBe "main"
    }

    test("saveScroll and consumeInitialScroll maintain scroll position") {
        val fakeUseCase = object : FetchBranchesUseCase {
            override suspend fun invoke(repoRef: RepoRef): BranchData {
                return createDummyBranchData("main")
            }
        }

        val viewModel = BranchesViewModel(
            repoRef = RepoRef("owner", "repo"),
            fetchBranchesUseCase = fakeUseCase
        )

        viewModel.consumeInitialScroll() shouldBe null

        viewModel.saveScroll(index = 3, offset = 50)

        val position = viewModel.consumeInitialScroll()
        position shouldNotBe null
        position?.index shouldBe 3
        position?.offset shouldBe 50

        viewModel.consumeInitialScroll() shouldBe null
    }

    test("refresh re-invokes usecase and reloads branch data") {
        var callCount = 0
        val fakeUseCase = object : FetchBranchesUseCase {
            override suspend fun invoke(repoRef: RepoRef): BranchData {
                callCount++
                return createDummyBranchData("branch-$callCount")
            }
        }

        val viewModel = BranchesViewModel(
            repoRef = RepoRef("owner", "repo"),
            fetchBranchesUseCase = fakeUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()
        val initialCallCount = callCount

        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        callCount shouldBe (initialCallCount + 1)
        viewModel.defaultBranch?.name shouldBe "branch-$callCount"
    }
})
