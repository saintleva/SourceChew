package com.github.saintleva.sourcechew.ui.screens.members

import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.usecase.FetchMembersUseCase
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.github.saintleva.sourcechew.ui.screens.PaginatedViewModel
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MembersViewModel(
    val org: String,
    private val fetchMembersUseCase: FetchMembersUseCase
) : PaginatedViewModel<Owner>() {

    private val _paginator = MutableStateFlow<Paginator<Owner>?>(null)
    val paginatorState = _paginator.asStateFlow()

    override val paginator: Paginator<Owner>?
        get() = _paginator.value

    private var loadJob: Job? = null
    private var scrollPosition: ScrollPosition? = null

    init {
        loadMembers()
    }

    private fun loadMembers() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val p = fetchMembersUseCase(org)
            _paginator.value = p
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

    fun refresh() {
        loadMembers()
    }
}
