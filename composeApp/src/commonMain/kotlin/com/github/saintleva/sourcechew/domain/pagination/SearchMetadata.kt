package com.github.saintleva.sourcechew.domain.pagination

import com.jamal_aliev.paginator.load.Metadata


class SearchMetadata(
    val totalCount: Int,
    val incompleteResults: Boolean
) : Metadata()
