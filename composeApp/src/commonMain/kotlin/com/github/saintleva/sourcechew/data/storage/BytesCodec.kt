package com.github.saintleva.sourcechew.data.storage


interface BytesCodec<T> {
    fun encode(value: T): ByteArray
    fun decode(bytes: ByteArray): T
}