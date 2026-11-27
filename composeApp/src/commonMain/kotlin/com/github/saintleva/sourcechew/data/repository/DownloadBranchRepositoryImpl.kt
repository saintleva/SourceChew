package com.github.saintleva.sourcechew.data.repository

import com.github.saintleva.sourcechew.data.network.DownloadBranchApiService
import com.github.saintleva.sourcechew.data.storage.BranchStorage
import com.github.saintleva.sourcechew.domain.coroutines.KeyedMutex
import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.repository.BranchDownloadListener
import com.github.saintleva.sourcechew.domain.repository.DownloadBranchRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import okio.FileSystem

class DownloadBranchRepositoryImpl(
    private val apiService: DownloadBranchApiService,
    private val branchStorage: BranchStorage,
    private val fileSystem: FileSystem,
    private val keyedMutex: KeyedMutex<BranchRef> = KeyedMutex()
) : DownloadBranchRepository {

    override suspend fun downloadAndExtractBranch(
        branchRef: BranchRef,
        listener: BranchDownloadListener
    ): Result<String, DetailsError> {
        return keyedMutex.withLock(branchRef) {
            val tempZipPath = branchStorage.getTempZipPath(branchRef)
            val targetDirPath = branchStorage.getTargetDirectoryPath(branchRef)

            try {
                val downloadResult = apiService.downloadBranchZip(
                    branchRef = branchRef,
                    tempZipPath = tempZipPath,
                    fileSystem = fileSystem,
                    onProgress = { bytesDownloaded, totalBytes ->
                        listener.onDownloadProgress(bytesDownloaded, totalBytes)
                    }
                )

                when (downloadResult) {
                    is Result.Success -> {
                        val extractedDir = branchStorage.extractZipArchive(
                            zipPath = tempZipPath,
                            targetDirPath = targetDirPath,
                            onExtractProgress = { filesExtracted, totalFiles ->
                                listener.onExtractProgress(filesExtracted, totalFiles)
                            }
                        )
                        Result.Success(extractedDir)
                    }
                    is Result.Failure -> {
                        Result.Failure(downloadResult.error)
                    }
                }
            } finally {
                branchStorage.deleteIfExists(tempZipPath)
            }
        }
    }
}
