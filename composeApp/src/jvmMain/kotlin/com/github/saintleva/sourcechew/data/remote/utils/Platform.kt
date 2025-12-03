package com.github.saintleva.sourcechew.data.remote.utils

import java.io.IOException
import java.nio.channels.UnresolvedAddressException


/**
 * JVM-specific implementation for both Android and Desktop.
 * Checks for exceptions common during network failures on the JVM.
 */
actual fun isNetworkException(e: Throwable): Boolean {
    return e is IOException || e is UnresolvedAddressException
}