package com.github.saintleva.sourcechew.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class RepoRef(
    val owner: String,
    val name: String
) {
    val cacheKey: String get() = "${owner.lowercase()}/${name.lowercase()}"
}
