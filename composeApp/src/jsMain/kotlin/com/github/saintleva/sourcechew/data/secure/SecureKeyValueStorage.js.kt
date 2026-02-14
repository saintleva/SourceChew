package com.github.saintleva.sourcechew.data.secure

import io.github.irgaly.kottage.Kottage
import kotlinx.browser.window
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.kottage.Kottage
import org.khronos.webgl.Uint8Array
import kotlin.js.js


class WebSecureStorage(private val kottage: Kottage) : SecureKeyValueStorage {

    private val mutex = Mutex()
    private var cryptoKey: dynamic = null // AES key

    private suspend fun getCryptoKey(): dynamic = mutex.withLock {
        if (cryptoKey == null) {
            cryptoKey = window.crypto.subtle.generateKey(
                js("{ name: 'AES-GCM', length: 256 }"), // key algorithm
                true,                                     // extractable
                arrayOf("encrypt", "decrypt")             // usages
            )
        }
        cryptoKey
    }

    private fun Uint8Array.toBase64(): String {
        val chars = CharArray(this.length) { i -> this[i].toInt().toChar() }
        return window.btoa(String(chars))
    }

    private fun String.base64ToUint8(): Uint8Array {
        val str = window.atob(this)
        return Uint8Array(str.length) { i -> str[i].code.toByte() }
    }

    override suspend fun write(keyName: String, value: String) {
        val k = getCryptoKey()
        val iv = Uint8Array(12)
        window.crypto.getRandomValues(iv)
        val data = window.TextEncoder().encode(value)
        val cipher = window.crypto.subtle.encrypt(js("{ name:'AES-GCM', iv:iv }"), k, data)
        val buf = Uint8Array(iv.length + Uint8Array(cipher).length)
        buf.set(iv, 0)
        buf.set(Uint8Array(cipher), iv.length)
        kottage.putString(keyName, buf.toBase64())
    }

    override suspend fun read(keyName: String): String? {
        val b64 = kottage.getString(keyName) ?: return null
        val bytes = b64.base64ToUint8()
        val iv = bytes.slice(0, 12)
        val cipher = bytes.slice(12)
        return try {
            val plain = window.crypto.subtle.decrypt(
                js("{ name:'AES-GCM', iv:iv }"),
                getCryptoKey(),
                cipher
            )
            window.TextDecoder().decode(plain) as String
        } catch (_: dynamic) {
            null
        }
    }

    override suspend fun remove(keyName: String) {
        kottage.remove(keyName)
    }
}