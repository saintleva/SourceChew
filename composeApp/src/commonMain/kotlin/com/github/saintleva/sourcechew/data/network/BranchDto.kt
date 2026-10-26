package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.RepoBranch
import kotlinx.serialization.Serializable

@Serializable
data class GithubBranchDto(
    val name: String,
    val commit: GithubBranchCommitDto,
    val `protected`: Boolean = false,
)

@Serializable
data class GithubBranchCommitDto(
    val sha: String,
    val url: String? = null,
)

fun GithubBranchDto.toDomain() = RepoBranch(
    name = name,
    commitSha = commit.sha,
)
