package com.github.saintleva.sourcechew.data.network.utils

import androidx.datastore.core.IOException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException


/**
 * Checks if a given exception is considered a network-related error on the current platform.
 */
expect fun isNetworkException(e: Throwable): Boolean

fun isBaseNetworkException(e: Throwable): Boolean {
    return e is IOException ||
            e is ConnectTimeoutException ||
            e is SocketTimeoutException ||
            e is HttpRequestTimeoutException
}