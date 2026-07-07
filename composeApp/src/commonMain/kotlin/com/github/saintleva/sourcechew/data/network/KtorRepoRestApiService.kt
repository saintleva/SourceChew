package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.appendCommonFilters
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoOnlyFlag
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchScope
import com.github.saintleva.sourcechew.domain.models.RepoSearchSort
import com.github.saintleva.sourcechew.domain.repository.FoundItemsBlock
import com.github.saintleva.sourcechew.domain.repository.SearchApiService
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse


class KtorRepoRestApiService(
    private val httpClient: HttpClient
): BaseKtorRestApiService<RepoSearchConditions, FoundRepo> {

    companion object {
        const val SEARCH_REPOSITORIES_ENDPOINT = "/search/repositories"

        private fun RepoSearchScope.toApiValue(): String = when (this) {
            RepoSearchScope.NAME -> "name"
            RepoSearchScope.DESCRIPTION -> "description"
            RepoSearchScope.README -> "readme"
        }

        private fun Set<RepoSearchScope>.toScopesApiValue(): String =
            joinToString(",") { it.toApiValue() }

        private fun RepoOnlyFlag.toApiValue(): String = when (this) {
            RepoOnlyFlag.PUBLIC -> "public"
            RepoOnlyFlag.PRIVATE -> "private"
            RepoOnlyFlag.FORK -> "fork"
            RepoOnlyFlag.ARCHIVED -> "archived"
            RepoOnlyFlag.MIRROR -> "mirror"
            RepoOnlyFlag.TEMPLATE -> "template"
        }

        private fun Set<RepoOnlyFlag>.toFlagsApiValue(): String =
            joinToString(" ") { "is:${it.toApiValue()}" }

        private fun RepoSearchSort.toApiValue(): String? = when (this) {
            RepoSearchSort.BEST_MATCH -> null
            RepoSearchSort.STARS -> "stars"
            RepoSearchSort.FORKS -> "forks"
            RepoSearchSort.UPDATED -> "updated"
        }
    }

    override suspend fun getHttpResponse(
        conditions: RepoSearchConditions,
        page: Int,
        pageSize: Int
    ): HttpResponse =
        httpClient.get(SEARCH_REPOSITORIES_ENDPOINT) {
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
                        if (conditions.onlyFlags.isNotEmpty()) {
                            append(" ${conditions.onlyFlags.toFlagsApiValue()}")
                        }
                    }.also { finalQuery ->
                        Napier.d(tag = "KtorRepoRestApiService") { "Query: \"$finalQuery\"" }
                    }
                }
            )

            // Append Repo-specific sort parameter (not in CommonFilters)
            conditions.sort.toApiValue()?.let { parameter("sort", it) }

            // Important: Disable the default exception throwing for 4xx and 5xx statuses
            // so we can handle them manually.
            expectSuccess = false
        }

    // Here we know the exact types (GithubRepoDto and FoundRepo) and can parse the response safely!
    override suspend fun deserializeSuccess(response: HttpResponse): FoundItemsBlock<FoundRepo> {
        return response.body<GithubSearchResponseDto<GithubRepoDto>>().toDomain { it.toDomain() }
    }
}