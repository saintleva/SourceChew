package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.Owner
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubOrgDto(
    val id: Long,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
)

fun GithubOrgDto.toDomain() = Owner(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
)
