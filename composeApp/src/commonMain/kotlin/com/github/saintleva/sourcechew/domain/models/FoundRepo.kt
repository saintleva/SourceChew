package com.github.saintleva.sourcechew.domain.models


data class FoundRepo(
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val ownerType: String,
    val description: String?,
    val language: String?,
    val stars: Int
)
