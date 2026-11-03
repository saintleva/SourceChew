package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.data.network.utils.parseInstant
import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.Commit
import kotlinx.serialization.Serializable

@Serializable
data class GithubBranchDto(
    val name: String,
    val commit: GithubBranchCommitDto,
)

@Serializable
data class GithubBranchCommitDto(
    val sha: String,
    val commit: GithubCommitDetailsDto? = null,
)

@Serializable
data class GithubCommitDetailsDto(
    val author: GithubCommitAuthorDto? = null,
    val committer: GithubCommitAuthorDto? = null,
)

@Serializable
data class GithubCommitAuthorDto(
    val date: String? = null,
)

fun GithubBranchDto.toDomain() = Branch(
    name = name,
    commit = Commit(
        sha = commit.sha,
        madeAt = parseInstant(commit.commit?.committer?.date ?: commit.commit?.author?.date),
    ),
)
