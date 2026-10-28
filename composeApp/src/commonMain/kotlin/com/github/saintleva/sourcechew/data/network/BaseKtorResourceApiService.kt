package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.result.DetailsError
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

interface BaseKtorResourceApiService<Request, out Answer> :
    BaseKtorApiService<Request, Answer, DetailsError> {

    override suspend fun mapErrorResponse(response: HttpResponse): DetailsError {
        return if (response.status == HttpStatusCode.NotFound) {
            DetailsError.ResourceNotFound
        } else {
            mapCommonApiError(response)
        }
    }
}
