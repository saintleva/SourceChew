package com.github.saintleva.sourcechew.domain.models

import io.ktor.http.HttpStatusCode


sealed interface FoundItem {
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
) : FoundItem

data class FoundOwner(
    override val id: Long,
    val login: String,
    override val url: String,
    val avatarUrl: String
) : FoundItem

sealed class FoundResult<out Item: FoundItem> {
    data class Success<out Item: FoundItem>(val value: FoundItem) : FoundResult<Item>()
    data class Error(val code: HttpStatusCode, val body: String) : FoundResult<Nothing>()
}