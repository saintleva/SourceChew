package com.github.saintleva.sourcechew.data.auth

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes


class DesktopSecureTokenStorage(
    private val storageDir: Path,
    private val crypto: CryptoEngine
) : SecureTokenStorage {

    private val tokenPath = storageDir / "auth_token.enc"

    override suspend fun read(): String? {
        if (!tokenPath.exists()) return null

        return try {
            val encrypted = tokenPath.readBytes()
            crypto.decrypt(encrypted).decodeToString()
        } catch (e: Exception) {
            e.printStackTrace()
            tokenPath.deleteIfExists()
            null
        }
    }

    override suspend fun write(token: String) {
        try {
            if (!storageDir.exists()) storageDir.createDirectories()
            val encrypted = crypto.encrypt(token.encodeToByteArray())
            tokenPath.writeBytes(encrypted)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun clear() {
        tokenPath.deleteIfExists()
    }
}