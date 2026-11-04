package com.github.saintleva.sourcechew.ui.screens.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.usecase.BranchData
import com.github.saintleva.sourcechew.domain.usecase.BranchDetailsResult
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
import kotlinx.coroutines.Job

class BranchesViewModel(
    val repoRef: RepoRef,
    private val fetchBranchesUseCase: FetchBranchesUseCase
) : ViewModel() {

    private val _branches = MutableStateFlow<BranchData?>(null)
    val branches = _branches.asStateFlow()

    val paginator: StateFlow<Paginator<Branch>?> = branches
        .map { it?.paginator }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = null
        )

    val defaultBranchResult: StateFlow<BranchDetailsResult?> = branches
        .map { it?.default }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = null
        )

    val defaultBranch: Branch?
        get() = (branches.value?.default as? BranchDetailsResult.Success)?.value

    private var loadJob: Job? = null

    private var scrollPosition: ScrollPosition? = null

    init {
        loadBranches()
    }

    private fun loadBranches() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _branches.value = fetchBranchesUseCase(repoRef)
            _branches.value?.paginator?.jump(BookmarkInt(1))
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

    fun refresh() {
        loadBranches()
    }

    fun restart() {
        viewModelScope.launch {
            paginator.value?.restart()
        }
    }

    fun loadPrevious() {
        viewModelScope.launch {
            paginator.value?.goPreviousPage()
        }
    }

    fun loadNext() {
        viewModelScope.launch {
            paginator.value?.goNextPage()
        }
    }
}
