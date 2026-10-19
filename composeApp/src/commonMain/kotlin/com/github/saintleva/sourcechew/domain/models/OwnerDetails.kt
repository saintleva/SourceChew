package com.github.saintleva.sourcechew.domain.models

import kotlin.time.Instant

data class OwnerDetails(
    override val id: Long,
    override val url: String,
    override val htmlUrl: String,
    override val createdAt: Instant?,
    override val updatedAt: Instant?,
    val login: String,
    val avatarUrl: String,
    val type: OwnerType,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val bio: String?,
    val publicRepos: Int,
    val publicGists: Int,
    val followers: Int?,
    val following: Int?
) : DetailsBase
