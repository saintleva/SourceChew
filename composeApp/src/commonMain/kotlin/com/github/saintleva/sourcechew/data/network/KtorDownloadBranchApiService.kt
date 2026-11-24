package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.isNetworkException
import com.github.saintleva.sourcechew.domain.models.BranchRef
import com.github.saintleva.sourcechew.domain.result.AppException
import com.github.saintleva.sourcechew.domain.result.CommonApiError
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlin.coroutines.cancellation.CancellationException
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

        try {
            val statement = httpClient.prepareGet(
                "/repos/${branchRef.repoRef.owner}/${branchRef.repoRef.name}/zipball/${branchRef.branch}"
            ) {
                expectSuccess = false
                onDownload { bytesSentTotal, contentLength ->
                    val total = if (contentLength != null && contentLength > 0L) contentLength else null
                    onProgress(bytesSentTotal, total)
                }
            }

            return statement.execute { response ->
                if (response.status.isSuccess()) {
                    try {
                        val channel: ByteReadChannel = response.body()
                        val buffer = ByteArray(DOWNLOAD_BUFFER_SIZE)
                        var totalBytesRead = 0L
                        val contentLength = response.contentLength()
                        val totalBytes = if (contentLength != null && contentLength > 0L) contentLength else null

                        fileSystem.sink(tempZipPath).buffer().use { sink ->
                            while (true) {
                                val read = channel.readAvailable(buffer, 0, buffer.size)
                                if (read == -1) break
                                if (read == 0) continue
                                sink.write(buffer, 0, read)
                                totalBytesRead += read.toLong()
                                onProgress(totalBytesRead, totalBytes)
                            }
                        }
                        Result.Success(Unit)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: AppException) {
                        throw e
                    } catch (e: Exception) {
                        throw DeserializationException(e)
                    }
                } else {
                    val error = mapErrorResponse(response)
                    Result.Failure(error)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppException) {
            throw e
        } catch (e: Exception) {
            val domainException = when {
                isNetworkException(e) -> NetworkException(e)
                e is ServerResponseException || e is RedirectResponseException ||
                        e is ClientRequestException -> UnknownInfrastructureException(e)
                else -> {
                    Napier.d(tag = logTag) {
                        "Caught unknown exception class: ${e::class.simpleName}, message: ${e.message}"
                    }
                    UnknownInfrastructureException(e)
                }
            }

            Napier.d(tag = logTag, throwable = domainException) {
                "before 'throw domainException'"
            }
            throw domainException
        }
    }

    private suspend fun mapErrorResponse(response: HttpResponse): DetailsError {
        return if (response.status == HttpStatusCode.NotFound) {
            DetailsError.ResourceNotFound
        } else {
            mapCommonApiError(response)
        }
    }

    private suspend fun mapCommonApiError(response: HttpResponse): CommonApiError {
        return when {
            response.status == HttpStatusCode.Unauthorized -> {
                CommonApiError.Unauthorized
            }
            response.status == HttpStatusCode.Forbidden -> {
                val body = response.bodyAsText()
                if (body.contains("rate limit", ignoreCase = true)) {
                    CommonApiError.RateLimitExceeded
                } else {
                    CommonApiError.CommonAccessError
                }
            }
            response.status == HttpStatusCode.TooManyRequests -> {
                CommonApiError.RateLimitExceeded
            }
            response.status.value in 500..599 -> {
                CommonApiError.ServerError
            }
            else -> {
                val unknownBody = response.bodyAsText()
                Napier.e(tag = "KtorDownloadBranchApiService") {
                    "Unknown error: ${response.status.value}, body: $unknownBody"
                }
                CommonApiError.UnknownApiError(response.status.value)
            }
        }
    }
}
