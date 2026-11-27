package com.github.saintleva.sourcechew.domain.models

import com.github.saintleva.sourcechew.domain.result.DownloadBranchError

sealed interface DownloadBranchStatus {

    data object Idle : DownloadBranchStatus

    /**
     * Indicates active downloading.
     * @param bytesDownloaded Total bytes downloaded so far.
     * @param totalBytes Total content length in bytes, or null if Content-Length header is absent.
     */
    data class Downloading(
        val bytesDownloaded: Long,
        val totalBytes: Long?
    ) : DownloadBranchStatus {
        val progress: Float?
            get() = if (totalBytes != null && totalBytes > 0L) {
                (bytesDownloaded.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
            } else {
                null
            }
    }

    /**
     * Indicates active archive extraction.
     * @param filesExtracted Number of files/directories extracted so far.
     * @param totalFiles Total count of files/directories in the archive, or null if unknown.
     */
    data class Extracting(
        val filesExtracted: Int = 0,
        val totalFiles: Int? = null
    ) : DownloadBranchStatus {
        val progress: Float?
            get() = if (totalFiles != null && totalFiles > 0) {
                (filesExtracted.toFloat() / totalFiles.toFloat()).coerceIn(0f, 1f)
            } else {
                null
            }
    }

    data class Success(
        val targetDirectory: String
    ) : DownloadBranchStatus

    data class Failed(
        val error: DownloadBranchError
    ) : DownloadBranchStatus
}
