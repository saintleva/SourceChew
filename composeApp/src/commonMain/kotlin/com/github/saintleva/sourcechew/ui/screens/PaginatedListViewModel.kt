package com.github.saintleva.sourcechew.ui.screens

import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class PaginatedListViewModel<Param, Item : Any>(
    val key: Param,
    private val fetchPaginator: suspend (Param) -> Paginator<Item>
) : PaginatedViewModel<Item>() {

    private val _paginatorStateFlow = MutableStateFlow<Paginator<Item>?>(null)
    val paginatorStateFlow = _paginatorStateFlow.asStateFlow()

    override val paginator: Paginator<Item>?
        get() = paginatorStateFlow.value

    private var scrollPosition: ScrollPosition? = null

    init {
        viewModelScope.launch {
            val p = fetchPaginator(key)
            _paginatorStateFlow.value = p
            p.jump(BookmarkInt(1))
        }
    }

    override fun consumeInitialScroll(): ScrollPosition? {
        val current = scrollPosition
        scrollPosition = null
        return current
    }

    override fun saveScroll(index: Int, offset: Int) {
        scrollPosition = ScrollPosition(index, offset)
    }
}
