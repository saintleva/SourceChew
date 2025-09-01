package com.github.saintleva.sourcechew.data.paging

import androidx.paging.PagingSource
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.time.Duration

class SearchPagingSource(
    private val searchApiService: SearchApiService,
    private val conditions: RepoSearchConditions
    private val eachCount: Int,
    private val delayImitation: Duration = Duration.ZERO,
    private val pageSize: Int = 10,
    private val searchDispatcher: CoroutineDispatcher = Dispatchers.IO
): PagingSource<Int, FoundRepo>() {
}