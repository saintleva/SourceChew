package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.models.RepoRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.buffer
import okio.use

class OkioBranchStorageTest : FunSpec({

    val dummyRepoRef = RepoRef("octocat", "Hello-World")
    val branchRef = BranchRef(dummyRepoRef, "feature/test#1")
    val fileSystem = FileSystem.SYSTEM
    val tempBaseDir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "sourcechew_test_${kotlin.random.Random.nextInt(100000)}"

    val storage = OkioBranchStorage(
        baseDir = tempBaseDir,
        fileSystem = fileSystem,
    )

    afterSpec {
        if (fileSystem.exists(tempBaseDir)) {
            fileSystem.deleteRecursively(tempBaseDir)
        }
    }

    test("getTempZipPath sanitizes branch name correctly") {
        val zipPath = storage.getTempZipPath(branchRef)
        zipPath.toString() shouldEndWith "feature_test_1.zip.tmp"
    }

    test("getTargetDirectoryPath sanitizes branch name correctly") {
        val targetDir = storage.getTargetDirectoryPath(branchRef)
        targetDir.toString() shouldEndWith "feature_test_1"
    }

    test("deleteIfExists deletes file and directory") {
        val testFile = tempBaseDir / "test_delete.txt"
        fileSystem.createDirectories(tempBaseDir)
        fileSystem.sink(testFile).buffer().use { sink ->
            sink.writeUtf8("hello")
        }

        fileSystem.exists(testFile) shouldBe true

        storage.deleteIfExists(testFile)
        fileSystem.exists(testFile) shouldBe false
    }

    test("extractZipArchive cleans up target directory when cancelled or error occurs") {
        val dummyZipPath = tempBaseDir / "dummy.zip"
        val targetDir = tempBaseDir / "target_dir"

        // Create dummy target directory structure to verify cleanup
        fileSystem.createDirectories(targetDir)
        val sampleFile = targetDir / "sample.txt"
        fileSystem.sink(sampleFile).buffer().use { sink ->
            sink.writeUtf8("dirty data")
        }

        // Trigger cancellation inside onExtractProgress callback
        var caughtException = false
        val job = launch {
            try {
                storage.extractZipArchive(
                    zipPath = dummyZipPath,
                    targetDirPath = targetDir,
                    onExtractProgress = { _, _ ->
                        cancel(CancellationException("Simulated cancellation"))
                    },
                )
            } catch (e: Throwable) {
                caughtException = true
            }
        }
        job.join()

        caughtException shouldBe true
        // Verify target directory was cleaned up on error/cancellation
        fileSystem.exists(targetDir) shouldBe false
    }
})
