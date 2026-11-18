package com.github.saintleva.sourcechew.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.ui.models.ScrollPosition
import com.jamal_aliev.paginator.offset.Paginator
import kotlinx.coroutines.launch

abstract class PaginatedViewModel<Item> : ViewModel() {

    abstract val paginator: Paginator<Item>?

    private var savedScrollPaginator: Paginator<*>? = null
    private var scrollPosition: ScrollPosition? = null

    open fun consumeInitialScroll(): ScrollPosition? {
        val currentPaginator = paginator
        val savedPaginator = savedScrollPaginator
        val matches = (savedPaginator == null || currentPaginator == null || savedPaginator === currentPaginator)
        if (matches) {
            val current = scrollPosition
            scrollPosition = null
            savedScrollPaginator = null
            return current
        } else {
            scrollPosition = null
            savedScrollPaginator = null
            return null
        }
    }

    open fun saveScroll(position: ScrollPosition) {
        savedScrollPaginator = paginator
        scrollPosition = position
    }

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
