package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.repository.FoundItemsBlock
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import com.github.saintleva.sourcechew.domain.result.SearchError
import com.github.saintleva.sourcechew.domain.result.SearchResult
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode


class PaginatedRequest<ItemSearchConditions>(
    val conditions: ItemSearchConditions,
    val page: Int,
    val pageSize: Int
)

interface BaseKtorSearchApiService<ItemSearchConditions, out FoundItem: FoundBase> :
    SearchApiService<ItemSearchConditions, FoundItem>,
    BaseKtorApiService<PaginatedRequest<ItemSearchConditions>, FoundItemsBlock<FoundItem>, SearchError> {

    suspend fun getHttpResponse(
        conditions: ItemSearchConditions,
        page: Int,
        pageSize: Int
    ) : HttpResponse

    override suspend fun getHttpResponse(request: PaginatedRequest<ItemSearchConditions>): HttpResponse =
        getHttpResponse(request.conditions, request.page, request.pageSize)

    override suspend fun mapErrorResponse(response: HttpResponse): SearchError {
        return if (response.status == HttpStatusCode.UnprocessableEntity) {
            val rawBody = response.bodyAsText()
            val errorMessage = try {
                val errorDto = response.body<GithubErrorResponseDto>()
                val details = errorDto.errors?.joinToString("; ") { detail ->
                    detail.message
                        ?: "Error in field '${detail.field}' (code: ${detail.code})"
                }
                val message = errorDto.message
                if (!details.isNullOrBlank()) {
                    if (message != null) "$message: $details" else details
                } else {
                    message ?: "Validation Error (422)"
                }
            } catch (e: Exception) {
                rawBody.takeIf { it.isNotBlank() } ?: "Validation Error (422)"
            }
            SearchError.Validation(errorMessage)
        } else {
            mapCommonApiError(response)
        }
    }

    override suspend fun searchItems(
        conditions: ItemSearchConditions,
        page: Int,
        pageSize: Int
    ): SearchResult<FoundItemsBlock<FoundItem>> =
        getAnswer(PaginatedRequest(conditions, page, pageSize))
}
