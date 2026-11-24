package com.github.saintleva.sourcechew.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class BranchRef(
    val repoRef: RepoRef,
    val branch: String
)
