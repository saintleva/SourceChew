package com.github.saintleva.sourcechew.domain.models

import kotlin.time.Instant


sealed interface FoundBase {
    val id: Long
}

data class FoundRepo(
    override val id: Long,
    val name: String,
    val owner: FoundOwner,
    val description: String?,
    val language: String?,
    val stars: Int,
    val updatedAt: Instant? = null
) : FoundBase

data class FoundOwner(
    override val id: Long,
    val type: OwnerType,
    val login: String,
    val avatarUrl: String
) : FoundBase