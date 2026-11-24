package com.github.saintleva.sourcechew.ui.screens.download

import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.models.DownloadBranchStatus
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.usecase.DownloadBranchUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadBranchViewModelTest : FunSpec({
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val dummyRepoRef = RepoRef("octocat", "Hello-World")
    val branch = "main"
    val branchRef = BranchRef(dummyRepoRef, branch)

    test("startDownload invokes usecase and updates status") {
        val fakeUseCase = object : DownloadBranchUseCase {
            override fun invoke(branchRef: BranchRef): Flow<DownloadBranchStatus> = flow {
                emit(DownloadBranchStatus.Downloading(0L, null))
                emit(DownloadBranchStatus.Success("/path/to/repo"))
            }
        }

        val viewModel = DownloadBranchViewModel(fakeUseCase)
        viewModel.downloadStatus.value shouldBe DownloadBranchStatus.Idle

        viewModel.startDownload(dummyRepoRef, branch)
        viewModel.activeTarget.value shouldBe branchRef

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.downloadStatus.value shouldBe DownloadBranchStatus.Success("/path/to/repo")
    }

    test("cancelDownload and reset restore Idle state") {
        val fakeUseCase = object : DownloadBranchUseCase {
            override fun invoke(branchRef: BranchRef): Flow<DownloadBranchStatus> = flow {
                emit(DownloadBranchStatus.Downloading(100L, 500L))
            }
        }

        val viewModel = DownloadBranchViewModel(fakeUseCase)
        viewModel.startDownload(dummyRepoRef, branch)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.downloadStatus.value shouldBe DownloadBranchStatus.Downloading(100L, 500L)

        viewModel.cancelDownload()
        viewModel.downloadStatus.value shouldBe DownloadBranchStatus.Idle
        viewModel.activeTarget.value shouldBe null
    }

    test("retry restarts download for active target") {
        var invocations = 0
        val fakeUseCase = object : DownloadBranchUseCase {
            override fun invoke(branchRef: BranchRef): Flow<DownloadBranchStatus> = flow {
                invocations++
                emit(DownloadBranchStatus.Success("/target/$invocations"))
            }
        }

        val viewModel = DownloadBranchViewModel(fakeUseCase)
        viewModel.startDownload(dummyRepoRef, branch)
        testDispatcher.scheduler.advanceUntilIdle()
        invocations shouldBe 1

        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()
        invocations shouldBe 2
        viewModel.downloadStatus.value shouldBe DownloadBranchStatus.Success("/target/2")
    }
})
