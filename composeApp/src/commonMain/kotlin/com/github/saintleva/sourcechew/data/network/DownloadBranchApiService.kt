package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import okio.FileSystem
import okio.Path

interface DownloadBranchApiService {
    /**
     * Downloads the ZIP archive for the specified repo branch and streams it to [tempZipPath].
     * Invokes [onProgress] with (bytesDownloaded, totalBytesOrNull).
     */
    suspend fun downloadBranchZip(
        repoRef: RepoRef,
        branch: String,
        tempZipPath: Path,
        fileSystem: FileSystem,
        onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
    ): Result<Unit, DetailsError>
}
