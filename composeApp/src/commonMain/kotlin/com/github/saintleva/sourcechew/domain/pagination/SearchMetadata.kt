package com.github.saintleva.sourcechew.domain.pagination

import com.jamal_aliev.paginator.core.load.Metadata


const val FIRST_PAGE = 1

class SearchMetadata(
    val totalCount: Int,
    val incompleteResults: Boolean
) : Metadata()
