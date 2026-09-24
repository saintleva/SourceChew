package com.github.saintleva.sourcechew.domain.models

import com.github.saintleva.sourcechew.domain.utils.Lens
import kotlinx.serialization.Serializable


@Serializable
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

        val UsePreviousRepoSearchLens = object : Lens<AppSettings, Boolean> {
            override fun get(whole: AppSettings): Boolean = whole.usePreviousRepoSearch
            override fun set(whole: AppSettings, part: Boolean): AppSettings = whole.copy(usePreviousRepoSearch = part)
        }

        val UsePreviousOwnerSearchLens = object : Lens<AppSettings, Boolean> {
            override fun get(whole: AppSettings): Boolean = whole.usePreviousOwnerSearch
            override fun set(whole: AppSettings, part: Boolean): AppSettings = whole.copy(usePreviousOwnerSearch = part)
        }
    }
}