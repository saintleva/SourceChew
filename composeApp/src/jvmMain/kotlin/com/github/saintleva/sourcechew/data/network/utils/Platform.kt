package com.github.saintleva.sourcechew.data.network.utils


/**
 * JVM-specific implementation for both Android and Desktop.
 * Checks for exceptions common during network failures on the JVM.
 */
actual fun isNetworkException(e: Throwable): Boolean {
    return isBaseNetworkException(e) ||
            e is java.net.UnknownHostException ||
            e is java.net.ConnectException
}