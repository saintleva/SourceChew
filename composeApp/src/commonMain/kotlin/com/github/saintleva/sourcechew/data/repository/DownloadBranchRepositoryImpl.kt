package com.github.saintleva.sourcechew.data.repository

import com.github.saintleva.sourcechew.data.network.DownloadBranchApiService
import com.github.saintleva.sourcechew.data.storage.BranchStorage
import com.github.saintleva.sourcechew.domain.models.RepoRef
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
        repoRef: RepoRef,
        branch: String,
        onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
    ): Result<Unit, DetailsError> {
        val tempZipPath = branchStorage.getTempZipPath(repoRef, branch)
        return apiService.downloadBranchZip(
            repoRef = repoRef,
            branch = branch,
            tempZipPath = tempZipPath,
            fileSystem = fileSystem,
            onProgress = onProgress
        )
    }

    override suspend fun extractBranchArchive(repoRef: RepoRef, branch: String): String {
        val tempZipPath = branchStorage.getTempZipPath(repoRef, branch)
        val targetDirPath = branchStorage.getTargetDirectoryPath(repoRef, branch)

        try {
            return branchStorage.extractZipArchive(tempZipPath, targetDirPath)
        } finally {
            branchStorage.deleteIfExists(tempZipPath)
        }
    }

    override suspend fun cleanupTempFile(repoRef: RepoRef, branch: String) {
        val tempZipPath = branchStorage.getTempZipPath(repoRef, branch)
        branchStorage.deleteIfExists(tempZipPath)
    }
}
