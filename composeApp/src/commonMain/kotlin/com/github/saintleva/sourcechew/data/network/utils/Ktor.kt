package com.github.saintleva.sourcechew.data.network.utils

import com.github.saintleva.sourcechew.domain.models.CommonFilters
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter


/**
 * Extension for HttpRequestBuilder to append common GitHub search parameters.
 */
fun HttpRequestBuilder.appendCommonFilters(
    common: CommonFilters,
    page: Int,
    pageSize: Int,
    buildFinalQuery: (String) -> String = { it }
) {
    // Construct the final 'q' parameter (base query + scopes/flags if needed)
    parameter("q", buildFinalQuery(common.query))

    // Mapping SearchOrder domain enum to GitHub API string values ("asc" / "desc")
    val orderValue = when (common.order) {
        SearchOrder.ASCENDING -> "asc"
        SearchOrder.DESCENDING -> "desc"
    }
    parameter("order", orderValue)

    // Standard GitHub API pagination parameters
    parameter("page", page)
    parameter("per_page", pageSize)
}