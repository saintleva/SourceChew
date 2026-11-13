package com.github.saintleva.sourcechew.ui.screens.userorgs

import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.usecase.FetchUserOrgsUseCase
import com.github.saintleva.sourcechew.domain.usecase.ScrollPosition
import com.github.saintleva.sourcechew.ui.screens.PaginatedViewModel
import com.jamal_aliev.paginator.offset.Paginator
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserOrgsViewModel(
    val username: String,
    private val fetchUserOrgsUseCase: FetchUserOrgsUseCase
) : PaginatedViewModel<Owner>() {

    private val paginatorState = MutableStateFlow<Paginator<Owner>?>(null)

    override val paginator: Paginator<Owner>?
        get() = paginatorState.value

    val currentPaginatorState = paginatorState.asStateFlow()

    private var scrollPosition: ScrollPosition? = null

    init {
        viewModelScope.launch {
            val p = fetchUserOrgsUseCase(username)
            paginatorState.value = p
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
