package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.models.DownloadBranchStatus
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.BranchDownloadListener
import com.github.saintleva.sourcechew.domain.repository.DownloadBranchRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.DownloadBranchError
import com.github.saintleva.sourcechew.domain.result.Result
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.toList

class DownloadBranchUseCaseTest : FunSpec({

    val dummyRepoRef = RepoRef("octocat", "Hello-World")
    val branch = "main"
    val branchRef = BranchRef(dummyRepoRef, branch)

    test("emits Downloading, Extracting with progress, and Success on happy path") {
        val fakeRepo = object : DownloadBranchRepository {
            override suspend fun downloadAndExtractBranch(
                branchRef: BranchRef,
                listener: BranchDownloadListener
            ): Result<String, DetailsError> {
                listener.onDownloadProgress(500L, 1000L)
                listener.onDownloadProgress(1000L, 1000L)
                listener.onExtractProgress(5, 10)
                listener.onExtractProgress(10, 10)
                return Result.Success("/path/to/extracted/main")
            }
        }

        val useCase = DownloadBranchUseCaseImpl(fakeRepo)
        val statuses = useCase(branchRef).toList()

        statuses.first() shouldBe DownloadBranchStatus.Downloading(0L, null)
        statuses.any { it is DownloadBranchStatus.Downloading && it.bytesDownloaded == 500L } shouldBe true
        statuses.any { it is DownloadBranchStatus.Downloading && it.bytesDownloaded == 1000L } shouldBe true
        statuses.any { it is DownloadBranchStatus.Extracting && it.filesExtracted == 5 && it.totalFiles == 10 } shouldBe true
        statuses.any { it is DownloadBranchStatus.Extracting && it.filesExtracted == 10 && it.totalFiles == 10 } shouldBe true
        statuses.last() shouldBe DownloadBranchStatus.Success("/path/to/extracted/main")
    }

    test("emits Failed on repository download error") {
        val fakeRepo = object : DownloadBranchRepository {
            override suspend fun downloadAndExtractBranch(
                branchRef: BranchRef,
                listener: BranchDownloadListener
            ): Result<String, DetailsError> {
                return Result.Failure(DetailsError.ResourceNotFound)
            }
        }

        val useCase = DownloadBranchUseCaseImpl(fakeRepo)
        val statuses = useCase(branchRef).toList()

        val lastStatus = statuses.last()
        lastStatus.shouldBeInstanceOf<DownloadBranchStatus.Failed>()
        (lastStatus.error as DownloadBranchError.Details).detailsError shouldBe DetailsError.ResourceNotFound
    }
})
