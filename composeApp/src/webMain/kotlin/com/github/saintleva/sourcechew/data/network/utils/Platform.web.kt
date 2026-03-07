package com.github.saintleva.sourcechew.data.network.utils


actual fun isNetworkException(e: Throwable): Boolean {
    if (isBaseNetworkException(e)) return true
    val msg = e.message?.lowercase() ?: ""
    return msg.contains("failed to fetch") || msg.contains("network error")
}