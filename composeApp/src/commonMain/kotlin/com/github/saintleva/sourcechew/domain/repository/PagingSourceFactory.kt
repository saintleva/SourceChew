package com.github.saintleva.sourcechew.domain.repository

import androidx.paging.PagingSource
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.usecase.Totality
import kotlinx.coroutines.flow.MutableStateFlow


interface PagingSourceFactory {
    fun createForRepoSearch(
        conditions: RepoSearchConditions,
        totalityState: MutableStateFlow<Totality?>
    ): PagingSource<Int, FoundRepo>
}