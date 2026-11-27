package com.github.saintleva.sourcechew.domain.repository

/**
 * Listener interface for tracking branch archive download and extraction progress.
 */
interface BranchDownloadListener {
    /**
     * Invoked periodically as data bytes are downloaded.
     * @param bytesDownloaded Total bytes downloaded so far.
     * @param totalBytes Content-Length of the file if known, or null otherwise.
     */
    fun onDownloadProgress(bytesDownloaded: Long, totalBytes: Long?)

    /**
     * Invoked periodically as zip entries are extracted.
     * @param filesExtracted Number of files/directories extracted so far.
     * @param totalFiles Total count of files/directories in the zip archive, or null if unknown.
     */
    fun onExtractProgress(filesExtracted: Int, totalFiles: Int?)
}
