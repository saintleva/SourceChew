package com.github.saintleva.sourcechew.data.storage

import androidx.datastore.core.okio.OkioSerializer
import okio.BufferedSink
import okio.BufferedSource


class CodecOkioSerializer<T>(
    override val defaultValue: T,
    private val codec: BytesCodec<T>
) : OkioSerializer<T> {

    override suspend fun readFrom(source: BufferedSource): T {
        return try {
            val bytes = source.readByteArray()
            codec.decode(bytes)
        } catch (_: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(value: T, sink: BufferedSink) {
        sink.write(codec.encode(value))
    }
}