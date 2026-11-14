package com.github.saintleva.sourcechew.ui.screens.members

import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.usecase.FetchMembersUseCase
import com.github.saintleva.sourcechew.ui.screens.PaginatedListViewModel

class MembersViewModel(
    val org: String,
    fetchMembersUseCase: FetchMembersUseCase,
) : PaginatedListViewModel<String, Owner>(
    key = org,
    fetchPaginator = fetchMembersUseCase::invoke,
)
