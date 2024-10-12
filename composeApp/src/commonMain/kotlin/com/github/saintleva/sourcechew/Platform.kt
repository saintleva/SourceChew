package com.github.saintleva.sourcechew

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform