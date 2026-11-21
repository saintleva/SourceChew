package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

data class DownloadBranchRequest(
    val repoRef: RepoRef,
    val branch: String,
    val tempZipPath: Path,
    val fileSystem: FileSystem,
    val onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
)

class KtorDownloadBranchApiService(
    private val httpClient: HttpClient
) : DownloadBranchApiService {

    override suspend fun downloadBranchZip(
        repoRef: RepoRef,
        branch: String,
        tempZipPath: Path,
        fileSystem: FileSystem,
        onProgress: (bytesDownloaded: Long, totalBytes: Long?) -> Unit
    ): Result<Unit, DetailsError> {
        val request = DownloadBranchRequest(
            repoRef = repoRef,
            branch = branch,
            tempZipPath = tempZipPath,
            fileSystem = fileSystem,
            onProgress = onProgress
        )

        val service = object : BaseKtorResourceApiService<DownloadBranchRequest, Unit> {
            override suspend fun getHttpResponse(request: DownloadBranchRequest): HttpResponse {
                val tempParent = request.tempZipPath.parent
                if (tempParent != null && !request.fileSystem.exists(tempParent)) {
                    request.fileSystem.createDirectories(tempParent)
                }

                return httpClient.get("/repos/${request.repoRef.owner}/${request.repoRef.name}/zipball/${request.branch}") {
                    expectSuccess = false
                    onDownload { bytesSentTotal, contentLength ->
                        val total = if (contentLength != null && contentLength > 0L) contentLength else null
                        request.onProgress(bytesSentTotal, total)
                    }
                }
            }

            override suspend fun deserializeSuccess(response: HttpResponse) {
                val channel: ByteReadChannel = response.body()
                val buffer = ByteArray(8192)
                var totalBytesRead = 0L
                val contentLength = response.contentLength()
                val totalBytes = if (contentLength != null && contentLength > 0L) contentLength else null

                request.fileSystem.sink(request.tempZipPath).buffer().use { sink ->
                    while (!channel.isClosedForRead) {
                        val read = channel.readAvailable(buffer, 0, buffer.size)
                        if (read <= 0) break
                        sink.write(buffer, 0, read)
                        totalBytesRead += read.toLong()
                        request.onProgress(totalBytesRead, totalBytes)
                    }
                    sink.flush()
                }
            }
        }

        return service.getAnswer(request)
    }
}
