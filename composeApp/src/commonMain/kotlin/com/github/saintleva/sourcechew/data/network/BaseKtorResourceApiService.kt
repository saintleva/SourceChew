package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.result.DetailsError
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

interface BaseKtorResourceApiService<Request, out Answer> :
    BaseKtorApiService<Request, Answer, DetailsError> {

    override suspend fun mapErrorResponse(response: HttpResponse): DetailsError =
        mapDetailsError(response)

    companion object {
        /**
         * Maps [HttpResponse] to [DetailsError], mapping 404 to [DetailsError.ResourceNotFound]
         * and delegating standard HTTP error codes to [BaseKtorApiService.mapCommonApiError].
         */
        suspend fun mapDetailsError(response: HttpResponse): DetailsError {
            return if (response.status == HttpStatusCode.NotFound) {
                DetailsError.ResourceNotFound
            } else {
                BaseKtorApiService.mapCommonApiError(response)
            }
        }
    }
}
