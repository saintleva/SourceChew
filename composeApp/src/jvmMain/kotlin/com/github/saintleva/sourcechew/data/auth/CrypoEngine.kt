package com.github.saintleva.sourcechew.data.auth


interface CryptoEngine {
    fun encrypt(data: ByteArray): ByteArray
    fun decrypt(data: ByteArray): ByteArray
}