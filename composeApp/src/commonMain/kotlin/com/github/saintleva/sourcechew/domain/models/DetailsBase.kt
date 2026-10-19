package com.github.saintleva.sourcechew.domain.models

import kotlin.time.Instant

sealed interface DetailsBase : FoundBase {
    override val id: Long
    override val url: String
    val htmlUrl: String
    val createdAt: Instant?
    val updatedAt: Instant?
}
