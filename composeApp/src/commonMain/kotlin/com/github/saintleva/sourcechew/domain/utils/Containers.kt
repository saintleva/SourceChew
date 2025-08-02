package com.github.saintleva.sourcechew.domain.utils


//TODO: remove this
//inline fun<T> Iterable<T>.makeSet(predicate: (T) -> Boolean): Set<T> {
//    val set = mutableSetOf<T>()
//    for (element in this) {
//        if (predicate(element)) {
//            set.add(element)
//        }
//    }
//    return set
//}

inline fun<K, V> Map<K, V>.makeSet(predicate: (V) -> Boolean): Set<K> {
    val set = mutableSetOf<K>()
    for (key in keys) {
        if (predicate(this[key]!!)) {
            set.add(key)
        }
    }
    return set
}