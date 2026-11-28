package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.repository.FoundItemsBlock
import com.github.saintleva.sourcechew.domain.repository.SearchRepository
import com.github.saintleva.sourcechew.domain.result.SearchError
import com.github.saintleva.sourcechew.domain.result.SearchResult
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode


class PaginatedRequest<ItemSearchConditions>(
    val conditions: ItemSearchConditions,
    val page: Int,
    val pageSize: Int
)

interface BaseKtorSearchRepository<ItemSearchConditions, out FoundItem: FoundBase> :
    SearchRepository<ItemSearchConditions, FoundItem>,
    BaseKtorApiService<PaginatedRequest<ItemSearchConditions>, FoundItemsBlock<FoundItem>, SearchError> {

    suspend fun getHttpResponse(
        conditions: ItemSearchConditions,
        page: Int,
        pageSize: Int
    ) : HttpResponse

    override suspend fun getHttpResponse(request: PaginatedRequest<ItemSearchConditions>): HttpResponse =
        getHttpResponse(request.conditions, request.page, request.pageSize)

    override suspend fun mapErrorResponse(response: HttpResponse): SearchError {
        return when (response.status) {
            HttpStatusCode.UnprocessableEntity -> {
                SearchError.Validation(parseErrorMessage(response, "Validation Error (422)"))
            }
            HttpStatusCode.NotFound -> SearchError.SearchNotFound
            else -> mapCommonApiError(response)
        }
    }

    override suspend fun searchItems(
        conditions: ItemSearchConditions,
        page: Int,
        pageSize: Int
    ): SearchResult<FoundItemsBlock<FoundItem>> =
        getAnswer(PaginatedRequest(conditions, page, pageSize))
}
