package com.github.saintleva.sourcechew.data.repository

import com.github.saintleva.sourcechew.data.network.DownloadBranchApiService
import com.github.saintleva.sourcechew.data.storage.BranchStorage
import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.repository.DownloadBranchRepository
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import okio.FileSystem

class DownloadBranchRepositoryImpl(
    private val apiService: DownloadBranchApiService,
    private val branchStorage: BranchStorage,
    private val fileSystem: FileSystem
) : DownloadBranchRepository {

    override suspend fun downloadBranchArchive(
        branchRef: BranchRef,
        onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
    ): Result<Unit, DetailsError> {
        val tempZipPath = branchStorage.getTempZipPath(branchRef)
        return apiService.downloadBranchZip(
            branchRef = branchRef,
            tempZipPath = tempZipPath,
            fileSystem = fileSystem,
            onProgress = onProgress
        )
    }

    override suspend fun extractBranchArchive(branchRef: BranchRef): String {
        val tempZipPath = branchStorage.getTempZipPath(branchRef)
        val targetDirPath = branchStorage.getTargetDirectoryPath(branchRef)

        try {
            return branchStorage.extractZipArchive(tempZipPath, targetDirPath)
        } finally {
            branchStorage.deleteIfExists(tempZipPath)
        }
    }

    override suspend fun cleanupTempFile(branchRef: BranchRef) {
        val tempZipPath = branchStorage.getTempZipPath(branchRef)
        branchStorage.deleteIfExists(tempZipPath)
    }
}
