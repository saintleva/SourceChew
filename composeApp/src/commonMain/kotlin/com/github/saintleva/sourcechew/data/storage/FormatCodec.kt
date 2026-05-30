package com.github.saintleva.sourcechew.data.storage

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat


class StringFormatCodec<T>(
    private val format: StringFormat,
    private val serializer: KSerializer<T>
) : BytesCodec<T> {
    override fun encode(value: T): ByteArray {
        val stringValue = format.encodeToString(serializer, value)
        return stringValue.encodeToByteArray()
    }

    override fun decode(bytes: ByteArray): T {
        val stringValue = bytes.decodeToString()
        return format.decodeFromString(serializer, stringValue)
    }
}

class BinaryFormatCodec<T>(
    private val format: BinaryFormat,
    private val serializer: KSerializer<T>
) : BytesCodec<T> {
    override fun encode(value: T): ByteArray {
        return format.encodeToByteArray(serializer, value)
    }

    override fun decode(bytes: ByteArray): T {
        return format.decodeFromByteArray(serializer, bytes)
    }
}