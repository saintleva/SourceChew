package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.appendCommonFilters
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.IntFilter
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.OwnerSearchScope
import com.github.saintleva.sourcechew.domain.models.OwnerSearchSort
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.repository.FoundItemsBlock
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse


class KtorOwnerRestApiService(
    private val httpClient: HttpClient
): BaseKtorRestApiService<OwnerSearchConditions, FoundOwner> {

    companion object {
        const val SEARCH_OWNERS_ENDPOINT = "/search/users"

        private fun OwnerSearchScope.toApiValue(): String = when (this) {
            OwnerSearchScope.LOGIN -> "login"
            OwnerSearchScope.FULLNAME -> "fullname"
            OwnerSearchScope.EMAIL -> "email"
        }

        private fun Set<OwnerSearchScope>.toScopesApiValue(): String =
            joinToString(",") { it.toApiValue() }

        private fun OwnerType.toApiValue(): String = when (this) {
            OwnerType.USER -> "user"
            OwnerType.ORGANIZATION -> "org"
        }

        /**
         * Converts a set of OwnerType to a GitHub API query string segment.
         * Returns null if all types or no types are selected, as no specific filtering is needed.
         */
        private fun Set<OwnerType>.toTypesApiValue(): String? {
            if (isEmpty() || size == OwnerType.entries.size) return null
            return joinToString(" ") { "type:${it.toApiValue()}" }
        }

        private fun IntFilter.toApiValue(): String = when (operator) {
            IntFilter.Operator.EQ -> "$value"
            IntFilter.Operator.GT -> ">$value"
            IntFilter.Operator.LT -> "<$value"
            IntFilter.Operator.GTE -> ">=$value"
            IntFilter.Operator.LTE -> "<=$value"
        }

        private fun OwnerSearchSort.toApiValue(): String? = when (this) {
            OwnerSearchSort.BEST_MATCH -> null
            OwnerSearchSort.FOLLOWERS -> "followers"
            OwnerSearchSort.REPOSITORIES -> "repositories"
            OwnerSearchSort.JOINED -> "joined"
        }
    }

    override suspend fun getHttpResponse(
        conditions: OwnerSearchConditions,
        page: Int,
        pageSize: Int
    ): HttpResponse =
        httpClient.get(SEARCH_OWNERS_ENDPOINT) {
            appendCommonFilters(
                common = conditions.common,
                page = page,
                pageSize = pageSize,
                buildFinalQuery = { baseQuery ->
                    buildString {
                        append(baseQuery)
                        if (conditions.inScope.isNotEmpty()) {
                            append(" in:${conditions.inScope.toScopesApiValue()}")
                        }
                        conditions.types.toTypesApiValue()?.let {
                            append(" $it")
                        }
                        conditions.repos?.let {
                            append(" repos:${it.toApiValue()}")
                        }
                        conditions.followers?.let {
                            append(" followers:${it.toApiValue()}")
                        }
                        conditions.location?.takeIf { it.isNotBlank() }?.let {
                            append(" location:\"$it\"")
                        }
                    }.also { finalQuery ->
                        Napier.d(tag = "KtorOwnerRestApiService") { "Query: \"$finalQuery\"" }
                    }
                }
            )

            conditions.sort.toApiValue()?.let { parameter("sort", it) }

            expectSuccess = false
        }

    override suspend fun deserializeSuccess(response: HttpResponse): FoundItemsBlock<FoundOwner> {
        return response.body<GithubSearchResponseDto<GithubOwnerDto>>().toDomain { it.toDomain() }
    }
}