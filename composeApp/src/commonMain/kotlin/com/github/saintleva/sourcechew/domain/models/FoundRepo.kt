package com.github.saintleva.sourcechew.domain.models

import io.ktor.http.HttpStatusCode


data class FoundRepo(
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val ownerType: String,
    val description: String?,
    val language: String?,
    val stars: Int
)

sealed class FoundRepoResult {
    data class Success(val repo: FoundRepo) : FoundRepoResult()
    data class Error(val code: HttpStatusCode, val body: String) : FoundRepoResult()
}