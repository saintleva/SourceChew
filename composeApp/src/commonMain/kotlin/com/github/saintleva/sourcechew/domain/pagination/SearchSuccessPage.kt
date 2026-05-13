package com.github.saintleva.sourcechew.domain.pagination

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.jamal_aliev.paginator.load.Metadata
import com.jamal_aliev.paginator.page.PageState
import com.jamal_aliev.paginator.page.PaginatorUiState


class SearchMetadata(
    val totalCount: Int,
    val incompleteResults: Boolean
) : Metadata()

class SearchSuccessPage<T>(
    page: Int,
    data: List<T>,
    override val metadata: SearchMetadata
) : PageState.SuccessPage<T>(page, data, metadata)

