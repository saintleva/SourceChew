package com.github.saintleva.sourcechew.domain.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Provides thread-safe and coroutine-safe Mutex synchronization keyed by [K].
 * Ensures that concurrent operations targeting the same key execute sequentially,
 * while operations with distinct keys execute concurrently.
 */
class KeyedMutex<K> {
    private val mutexMap = mutableMapOf<K, MutexHolder>()
    private val lock = Mutex()

    private class MutexHolder {
        val mutex = Mutex()
        var refCount = 0
    }

    /**
     * Executes the given [action] under a lock associated with [key].
     */
    suspend fun <T> withLock(key: K, action: suspend () -> T): T {
        val holder = lock.withLock {
            mutexMap.getOrPut(key) { MutexHolder() }.also { it.refCount++ }
        }
        try {
            return holder.mutex.withLock {
                action()
            }
        } finally {
            lock.withLock {
                holder.refCount--
                if (holder.refCount == 0) {
                    mutexMap.remove(key)
                }
            }
        }
    }
}
