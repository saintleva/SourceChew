package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface DownloadBranchRepository {
    /**
     * Downloads the branch ZIP archive to a temporary file while invoking [onProgress].
     */
    suspend fun downloadBranchArchive(
        branchRef: BranchRef,
        onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
    ): Result<Unit, DetailsError>

    /**
     * Extracts the downloaded ZIP archive to local storage and cleans up the temporary ZIP file.
     * Returns the target directory path string.
     */
    suspend fun extractBranchArchive(branchRef: BranchRef): String

    /**
     * Cleans up temporary download artifacts if present.
     */
    suspend fun cleanupTempFile(branchRef: BranchRef)
}
