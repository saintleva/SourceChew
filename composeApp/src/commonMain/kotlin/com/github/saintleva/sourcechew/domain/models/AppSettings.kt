package com.github.saintleva.sourcechew.domain.models


data class AppSettings(
    val paginationPageSize: Int,
    val usePreviousRepoSearch: Boolean,
    val usePreviousOwnerSearch: Boolean
) {
    companion object {
        val default = AppSettings(
            paginationPageSize = 30,
            usePreviousRepoSearch = false,
            usePreviousOwnerSearch = false
        )
        val paginationPageSizeRange = 1..100
    }
}