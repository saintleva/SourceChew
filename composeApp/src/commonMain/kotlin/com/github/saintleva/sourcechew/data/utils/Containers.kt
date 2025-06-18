package com.github.saintleva.sourcechew.data.utils


//TODO: rename "transform"
inline fun<T> Iterable<T>.makeSet(crossinline predicate: (T) -> Boolean): Set<T> {
    val set = mutableSetOf<T>()
    for (element in this) {
        if (predicate(element)) {
            set.add(element)
        }
    }
    return set
}