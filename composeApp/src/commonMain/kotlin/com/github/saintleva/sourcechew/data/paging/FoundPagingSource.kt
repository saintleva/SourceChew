package com.github.saintleva.sourcechew.data.paging

import app.cash.paging.PagingSource
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions

class FoundPagingSource(
    private val searchApiService: SearchApiService,
    private val conditions: RepoSearchConditions
): PagingSource<Int, FoundRepo>() {
}