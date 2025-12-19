package com.github.saintleva.sourcechew.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey


fun<T: NavKey> NavBackStack<T>.pop() {
    if (isNotEmpty()) {
        removeAt(lastIndex)
    }
}