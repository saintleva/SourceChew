package com.github.saintleva.sourcechew.data.utils


//TODO: Make this function inline and not suspend
suspend fun<T> Iterable<T>.makeSet(predicate: suspend (T) -> Boolean): Set<T> {
    val set = mutableSetOf<T>()
    for (element in this) {
        if (predicate(element)) {
            set.add(element)
        }
    }
    return set
}