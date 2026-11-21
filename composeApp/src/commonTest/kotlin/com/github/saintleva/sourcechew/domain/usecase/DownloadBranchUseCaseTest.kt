package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.DownloadBranchStatus
import com.github.saintleva.sourcechew.domain.models.RepoRef
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

    test("emits Downloading, Extracting, and Success on happy path") {
        val fakeRepo = object : DownloadBranchRepository {
            override suspend fun downloadBranchArchive(
                repoRef: RepoRef,
                branch: String,
                onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
            ): Result<Unit, DetailsError> {
                onProgress(500L, 1000L)
                onProgress(1000L, 1000L)
                return Result.Success(Unit)
            }

            override suspend fun extractBranchArchive(repoRef: RepoRef, branch: String): String {
                return "/path/to/extracted/main"
            }

            override suspend fun cleanupTempFile(repoRef: RepoRef, branch: String) = Unit
        }

        val useCase = DownloadBranchUseCaseImpl(fakeRepo)
        val statuses = useCase(dummyRepoRef, branch).toList()

        statuses.first() shouldBe DownloadBranchStatus.Downloading(0L, null)
        statuses.any { it is DownloadBranchStatus.Downloading && it.bytesDownloaded == 500L } shouldBe true
        statuses.any { it is DownloadBranchStatus.Downloading && it.bytesDownloaded == 1000L } shouldBe true
        statuses.any { it is DownloadBranchStatus.Extracting } shouldBe true
        statuses.last() shouldBe DownloadBranchStatus.Success("/path/to/extracted/main")
    }

    test("emits Failed on repository download error") {
        var cleanedUp = false
        val fakeRepo = object : DownloadBranchRepository {
            override suspend fun downloadBranchArchive(
                repoRef: RepoRef,
                branch: String,
                onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
            ): Result<Unit, DetailsError> {
                return Result.Failure(DetailsError.ResourceNotFound)
            }

            override suspend fun extractBranchArchive(repoRef: RepoRef, branch: String): String {
                error("Should not be called")
            }

            override suspend fun cleanupTempFile(repoRef: RepoRef, branch: String) {
                cleanedUp = true
            }
        }

        val useCase = DownloadBranchUseCaseImpl(fakeRepo)
        val statuses = useCase(dummyRepoRef, branch).toList()

        cleanedUp shouldBe true
        val lastStatus = statuses.last()
        lastStatus.shouldBeInstanceOf<DownloadBranchStatus.Failed>()
        (lastStatus.error as DownloadBranchError.Details).detailsError shouldBe DetailsError.ResourceNotFound
    }
})
