package com.github.saintleva.sourcechew.domain.repository

import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result

interface DownloadBranchRepository {
    /**
     * Downloads and extracts the branch ZIP archive while reporting progress via [listener].
     * Guarantees atomic, serialized execution per [branchRef] and automatic cleanup of temporary files.
     * Returns the target directory path on success or [DetailsError] on failure.
     */
    suspend fun downloadAndExtractBranch(
        branchRef: BranchRef,
        listener: BranchDownloadListener
    ): Result<String, DetailsError>
}
