package com.github.saintleva.sourcechew.ui.screens.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.repository.BranchesApiService
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.domain.usecase.FetchBranchesUseCase
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BranchesViewModel(
    val owner: String,
    val repo: String,
    val defaultBranch: String?,
    private val fetchBranchesUseCase: FetchBranchesUseCase,
    private val branchesApiService: BranchesApiService
) : ViewModel() {

    private val _paginator = MutableStateFlow<Paginator<Branch>?>(null)
    val paginator: StateFlow<Paginator<Branch>?> = _paginator.asStateFlow()

    private val _defaultBranchCommitSha = MutableStateFlow<String?>(null)
    val defaultBranchCommitSha: StateFlow<String?> = _defaultBranchCommitSha.asStateFlow()

    private var scrollPosition: ScrollPosition? = null

    init {
        loadBranches()
        fetchDefaultBranchCommitSha()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            val p = fetchBranchesUseCase(owner, repo)
            _paginator.value = p
            p.jump(BookmarkInt(1))
        }
    }

    private fun fetchDefaultBranchCommitSha() {
        if (!defaultBranch.isNullOrBlank()) {
            viewModelScope.launch {
                when (val result = branchesApiService.getSingleBranch(owner, repo, defaultBranch)) {
                    is Result.Success -> _defaultBranchCommitSha.value = result.value.commitSha
                    is Result.Failure -> { /* no-op */ }
                }
            }
        }
    }

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
            _defaultBranchCommitSha.value = null
            _paginator.value?.restart()
            fetchDefaultBranchCommitSha()
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
