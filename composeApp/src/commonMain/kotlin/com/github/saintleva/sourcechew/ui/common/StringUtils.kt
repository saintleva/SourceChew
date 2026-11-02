package com.github.saintleva.sourcechew.ui.common

/**
 * Truncates the string to the specified maximum length and appends an ellipsis if truncated.
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (length > maxLength) {
        take(maxLength) + ellipsis
    } else {
        this
    }
}
