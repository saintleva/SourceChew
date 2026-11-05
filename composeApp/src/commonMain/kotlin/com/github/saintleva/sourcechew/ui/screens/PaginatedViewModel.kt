package com.github.saintleva.sourcechew.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.jamal_aliev.paginator.offset.Paginator
import kotlinx.coroutines.launch

abstract class PaginatedViewModel<Item> : ViewModel() {

    abstract val paginator: Paginator<Item>?

    abstract fun consumeInitialScroll(): ScrollPosition?

    abstract fun saveScroll(index: Int, offset: Int)

    fun restart() {
        viewModelScope.launch { paginator?.restart() }
    }

    fun loadPrevious() {
        viewModelScope.launch { paginator?.goPreviousPage() }
    }

    fun loadNext() {
        viewModelScope.launch { paginator?.goNextPage() }
    }
}