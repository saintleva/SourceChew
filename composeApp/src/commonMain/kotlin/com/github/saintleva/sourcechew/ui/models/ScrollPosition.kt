package com.github.saintleva.sourcechew.ui.models

data class ScrollPosition(
    val index: Int,
    val offset: Int
) {
    fun isAtStart() = index == 0 && offset == 0

    companion object {
        val Zero = ScrollPosition(0, 0)
    }
}
