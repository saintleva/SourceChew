package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.models.DownloadBranchStatus
import com.github.saintleva.sourcechew.domain.repository.BranchDownloadListener
import com.github.saintleva.sourcechew.domain.repository.DownloadBranchRepository
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.DownloadBranchError
import com.github.saintleva.sourcechew.domain.result.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

interface DownloadBranchUseCase {
    operator fun invoke(branchRef: BranchRef): Flow<DownloadBranchStatus>
}

class DownloadBranchUseCaseImpl(
    private val downloadBranchRepository: DownloadBranchRepository
) : DownloadBranchUseCase {

    override fun invoke(branchRef: BranchRef): Flow<DownloadBranchStatus> = channelFlow {
        send(DownloadBranchStatus.Downloading(bytesDownloaded = 0L, totalBytes = null))

        try {
            val result = downloadBranchRepository.downloadAndExtractBranch(
                branchRef = branchRef,
                listener = object : BranchDownloadListener {
                    override fun onDownloadProgress(bytesDownloaded: Long, totalBytes: Long?) {
                        trySend(DownloadBranchStatus.Downloading(bytesDownloaded, totalBytes))
                    }

                    override fun onExtractProgress(filesExtracted: Int, totalFiles: Int?) {
                        trySend(DownloadBranchStatus.Extracting(filesExtracted, totalFiles))
                    }
                }
            )

            when (result) {
                is Result.Success -> {
                    send(DownloadBranchStatus.Success(result.value))
                }
                is Result.Failure -> {
                    send(DownloadBranchStatus.Failed(DownloadBranchError.Details(result.error)))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppException) {
            send(DownloadBranchStatus.Failed(DownloadBranchError.Infrastructure(e)))
        } catch (e: Exception) {
            send(DownloadBranchStatus.Failed(DownloadBranchError.Unknown(e)))
        }
    }
}
