package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

private const val DOWNLOAD_BUFFER_SIZE = 8192

class KtorDownloadBranchApiService(
    private val httpClient: HttpClient
) : DownloadBranchApiService {

    override suspend fun downloadBranchZip(
        branchRef: BranchRef,
        tempZipPath: Path,
        fileSystem: FileSystem,
        onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
    ): Result<Unit, DetailsError> {
        val logTag = "KtorDownloadBranchApiService"
        val tempParent = tempZipPath.parent
        if (tempParent != null && !fileSystem.exists(tempParent)) {
            fileSystem.createDirectories(tempParent)
        }

        return executeSafeApiCall(logTag) {
            val statement = httpClient.prepareGet(
                "/repos/${branchRef.repoRef.owner}/${branchRef.repoRef.name}/zipball/${branchRef.branch}"
            ) {
                expectSuccess = false
                onDownload { bytesSentTotal, contentLength ->
                    val total = if ((contentLength != null) && (contentLength > 0L)) contentLength else null
                    onProgress(bytesSentTotal, total)
                }
            }

            statement.execute { response ->
                if (response.status.isSuccess()) {
                    streamChannelToFile(response, tempZipPath, fileSystem, onProgress)
                    Result.Success(Unit)
                } else {
                    Result.Failure(BaseKtorResourceApiService.mapDetailsError(response))
                }
            }
        }
    }

    private suspend fun streamChannelToFile(
        response: HttpResponse,
        tempZipPath: Path,
        fileSystem: FileSystem,
        onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
    ) {
        val channel: ByteReadChannel = response.body()
        val buffer = ByteArray(DOWNLOAD_BUFFER_SIZE)
        var totalBytesRead = 0L
        val contentLength = response.contentLength()
        val totalBytes = if ((contentLength != null) && (contentLength > 0L)) contentLength else null

        fileSystem.sink(tempZipPath).buffer().use { sink ->
            while (true) {
                // Ensure the enclosing coroutine context is active before reading next chunk
                currentCoroutineContext().ensureActive()
                val read = channel.readAvailable(buffer, 0, buffer.size)
                if (read == -1) break
                if (read == 0) continue
                sink.write(buffer, 0, read)
                totalBytesRead += read.toLong()
                onProgress(totalBytesRead, totalBytes)
            }
        }
    }
}
