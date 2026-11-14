package com.github.saintleva.sourcechew.ui.screens.userorgs

import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.usecase.FetchUserOrgsUseCase
import com.github.saintleva.sourcechew.ui.screens.PaginatedListViewModel

class UserOrgsViewModel(
    val username: String,
    fetchUserOrgsUseCase: FetchUserOrgsUseCase,
) : PaginatedListViewModel<String, Owner>(
    key = username,
    fetchPaginator = fetchUserOrgsUseCase::invoke,
)
