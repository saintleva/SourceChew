package com.github.saintleva.sourcechew.ui.screens.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.Commit
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.BranchesApiService
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.usecase.BranchPaginating
import com.github.saintleva.sourcechew.domain.usecase.FetchBranchesUseCase
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.github.saintleva.sourcechew.ui.utils.WhileUiSubscribed
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BranchesViewModel(
    val repoRef: RepoRef,
    val defaultBranch: String?,
    private val fetchBranchesUseCase: FetchBranchesUseCase,
    private val branchesApiService: BranchesApiService
) : ViewModel() {


    private val _branches = MutableStateFlow<BranchPaginating?>(null)
    val branches = _branches.asStateFlow()

    private var scrollPosition: ScrollPosition? = null

    init {
        loadBranches()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            _branches.value = fetchBranchesUseCase(repoRef, defaultBranch)
            _branches.value?.paginator?.jump(BookmarkInt(1))
        }
    }

    //TODO: Remove this
//
//    private fun fetchDefaultBranchCommit() {
//        if (!defaultBranch.isNullOrBlank()) {
//            viewModelScope.launch {
//                when (val result = branchesApiService.getSingleBranch(repoRef, defaultBranch)) {
//                    is Result.Success -> _defaultBranchCommit.value = result.value.commit
//                    is Result.Failure -> { /* no-op */ }
//                }
//            }
//        }
//    }

    fun consumeInitialScroll(): ScrollPosition? {
        val current = scrollPosition
        scrollPosition = null
        return current
    }

    fun saveScroll(index: Int, offset: Int) {
        scrollPosition = ScrollPosition(index, offset)
    }

    fun restart() {
        viewModelScope.launch {
            _defaultBranchCommit.value = null
            _paginator.value?.restart()
            fetchDefaultBranchCommit()
        }
    }

    fun loadPrevious() {
        viewModelScope.launch {
            _paginator.value?.goPreviousPage()
        }
    }

    fun loadNext() {
        viewModelScope.launch {
            _paginator.value?.goNextPage()
        }
    }
}
