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

    data object Extracting : DownloadBranchStatus

    data class Success(
        val targetDirectory: String
    ) : DownloadBranchStatus

    data class Failed(
        val error: DownloadBranchError
    ) : DownloadBranchStatus
}
