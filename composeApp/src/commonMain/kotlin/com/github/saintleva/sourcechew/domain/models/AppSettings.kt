package com.github.saintleva.sourcechew.domain.models

import com.github.saintleva.sourcechew.domain.utils.Lens
import kotlinx.serialization.Serializable


@Serializable
data class AppSettings(
    val paginationPageSize: Int,
    val usePreviousRepoSearch: Boolean,
    val usePreviousOwnerSearch: Boolean,
    val maxRepoDescriptionLength: Int = 200,
    val maxFoundRepoDescriptionLength: Int = 100
) {
    companion object {
        val default = AppSettings(
            paginationPageSize = 30,
            usePreviousRepoSearch = false,
            usePreviousOwnerSearch = false,
            maxRepoDescriptionLength = 200,
            maxFoundRepoDescriptionLength = 100
        )
        val paginationPageSizeRange = 1..100
        val maxRepoDescriptionLengthRange = 50..1000
        val maxFoundRepoDescriptionLengthRange = 20..300

        val UsePreviousRepoSearchLens = object : Lens<AppSettings, Boolean> {
            override fun get(whole: AppSettings): Boolean = whole.usePreviousRepoSearch
            override fun set(whole: AppSettings, part: Boolean): AppSettings = whole.copy(usePreviousRepoSearch = part)
        }

        val UsePreviousOwnerSearchLens = object : Lens<AppSettings, Boolean> {
            override fun get(whole: AppSettings): Boolean = whole.usePreviousOwnerSearch
            override fun set(whole: AppSettings, part: Boolean): AppSettings = whole.copy(usePreviousOwnerSearch = part)
        }

        val MaxRepoDescriptionLengthLens = object : Lens<AppSettings, Int> {
            override fun get(whole: AppSettings): Int = whole.maxRepoDescriptionLength
            override fun set(whole: AppSettings, part: Int): AppSettings = whole.copy(maxRepoDescriptionLength = part)
        }

        val MaxFoundRepoDescriptionLengthLens = object : Lens<AppSettings, Int> {
            override fun get(whole: AppSettings): Int = whole.maxFoundRepoDescriptionLength
            override fun set(whole: AppSettings, part: Int): AppSettings = whole.copy(maxFoundRepoDescriptionLength = part)
        }
    }
}