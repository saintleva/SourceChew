package com.github.saintleva.sourcechew.test

import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Fake implementation of [ConfigStore] for testing.
 */
class FakeConfigStore<T>(initial: T) : ConfigStore<T> {
    private val _config = MutableStateFlow(initial)
    override val config: Flow<T> = _config
    override suspend fun update(updater: (T) -> T) {
        _config.update(updater)
    }
}
