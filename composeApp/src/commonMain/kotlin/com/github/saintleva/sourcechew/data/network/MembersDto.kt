package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.models.OwnerType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubMemberDto(
    val id: Long,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
)

fun GithubMemberDto.toDomain() = Owner(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
)
