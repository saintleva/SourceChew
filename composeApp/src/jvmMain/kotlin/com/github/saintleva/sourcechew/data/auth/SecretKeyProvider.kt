package com.github.saintleva.sourcechew.data.auth

import javax.crypto.SecretKey


interface SecretKeyProvider {

    companion object {
        const val KEY_SIZE = 256
    }

    suspend fun getOrCreateKey(): SecretKey
}