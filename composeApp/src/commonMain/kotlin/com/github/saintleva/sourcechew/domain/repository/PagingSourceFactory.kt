package com.github.saintleva.sourcechew.domain.repository

import androidx.paging.PagingSource
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions


interface PagingSourceFactory {
    fun createForRepoSearch(conditions: RepoSearchConditions): PagingSource<Int, FoundRepo>
}