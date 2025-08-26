package com.github.saintleva.sourcechew.domain.models


data class FoundRepo(
    val author: String,
    val name: String,
    val description: String,
    val language: String,
    val stars: Int
)
