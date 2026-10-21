package com.github.saintleva.sourcechew.domain.models

import io.ktor.http.HttpStatusCode


sealed interface FoundBase {
    val id: Long
    val url: String
}

data class FoundRepo(
    override val id: Long,
    val name: String,
    val fullName: String,
    override val url: String,
    val owner: FoundOwner,
    val description: String?,
    val language: String?,
    val stars: Int
) : FoundBase

data class FoundOwner(
    override val id: Long,
    val type: OwnerType,
    val login: String,
    override val url: String,
    val avatarUrl: String
) : FoundBase