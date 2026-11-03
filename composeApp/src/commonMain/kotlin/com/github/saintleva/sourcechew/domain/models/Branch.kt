package com.github.saintleva.sourcechew.domain.models

import kotlin.time.Instant

data class Commit(
    val sha: String,
    val madeAt: Instant? = null
)

data class Branch(
    val name: String,
    val commit: Commit
)
