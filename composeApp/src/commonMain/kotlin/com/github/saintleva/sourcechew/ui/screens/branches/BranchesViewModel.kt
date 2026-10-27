package com.github.saintleva.sourcechew.ui.screens.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.RepoBranch
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
    private val fetchBranchesUseCase: FetchBranchesUseCase
) : ViewModel() {

    private val _paginator = MutableStateFlow<Paginator<RepoBranch>?>(null)
    val paginator: StateFlow<Paginator<RepoBranch>?> = _paginator.asStateFlow()

    private var scrollPosition: ScrollPosition? = null

    init {
        loadBranches()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            val p = fetchBranchesUseCase(owner, repo)
            _paginator.value = p
            p.jump(BookmarkInt(1))
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
            _paginator.value?.restart()
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
