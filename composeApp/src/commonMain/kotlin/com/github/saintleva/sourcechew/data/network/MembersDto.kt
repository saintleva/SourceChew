package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.OwnerType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubMemberDto(
    val id: Long,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val type: String = "User",
    val url: String? = null,
    @SerialName("html_url") val htmlUrl: String? = null,
)

fun GithubMemberDto.toDomain() = FoundOwner(
    id = id,
    type = when (type.lowercase()) {
        "user" -> OwnerType.USER
        "organization" -> OwnerType.ORGANIZATION
        else -> OwnerType.USER
    },
    login = login,
    avatarUrl = avatarUrl,
)
