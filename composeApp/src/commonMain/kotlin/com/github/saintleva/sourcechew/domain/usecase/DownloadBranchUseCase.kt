package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.DownloadBranchStatus
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.DownloadBranchRepository
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.DownloadBranchError
import com.github.saintleva.sourcechew.domain.result.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

interface DownloadBranchUseCase {
    operator fun invoke(repoRef: RepoRef, branch: String): Flow<DownloadBranchStatus>
}

class DownloadBranchUseCaseImpl(
    private val downloadBranchRepository: DownloadBranchRepository
) : DownloadBranchUseCase {

    override fun invoke(repoRef: RepoRef, branch: String): Flow<DownloadBranchStatus> = channelFlow {
        send(DownloadBranchStatus.Downloading(bytesDownloaded = 0L, totalBytes = null))

        try {
            val downloadResult = downloadBranchRepository.downloadBranchArchive(
                repoRef = repoRef,
                branch = branch
            ) { bytesDownloaded, totalBytes ->
                trySend(DownloadBranchStatus.Downloading(bytesDownloaded, totalBytes))
            }

            when (downloadResult) {
                is Result.Success -> {
                    send(DownloadBranchStatus.Extracting)
                    val targetDir = downloadBranchRepository.extractBranchArchive(repoRef, branch)
                    send(DownloadBranchStatus.Success(targetDir))
                }
                is Result.Failure -> {
                    downloadBranchRepository.cleanupTempFile(repoRef, branch)
                    send(DownloadBranchStatus.Failed(DownloadBranchError.Details(downloadResult.error)))
                }
            }
        } catch (e: CancellationException) {
            downloadBranchRepository.cleanupTempFile(repoRef, branch)
            throw e
        } catch (e: AppException) {
            downloadBranchRepository.cleanupTempFile(repoRef, branch)
            send(DownloadBranchStatus.Failed(DownloadBranchError.Infrastructure(e)))
        } catch (e: Exception) {
            downloadBranchRepository.cleanupTempFile(repoRef, branch)
            send(DownloadBranchStatus.Failed(DownloadBranchError.Unknown(e)))
        }
    }
}
