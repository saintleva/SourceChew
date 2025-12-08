package com.github.saintleva.sourcechew.data.network.utils


/**
 * Checks if a given exception is considered a network-related error on the current platform.
 */
expect fun isNetworkException(e: Throwable): Boolean