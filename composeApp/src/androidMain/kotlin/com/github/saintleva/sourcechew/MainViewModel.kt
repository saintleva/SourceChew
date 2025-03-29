package com.github.saintleva.sourcechew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


class MainViewModel : ViewModel() {

    private val configRepository: ConfigRepository by inject(ConfigRepository::class.java)

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Napier.d(tag = "MainViewModel") { "before loadData()" }
            Napier.d(tag = "ConfigRepositoryImpl") {
                "Created object in MainViewModel " +
                        System.identityHashCode(configRepository).toUInt().toString(radix = 16)
            }
            configRepository.loadData()
            Napier.d(tag = "ConfigRepositoryImpl") {
                "Created object in MainViewModel after loadData() " +
                        System.identityHashCode(configRepository).toUInt().toString(radix = 16)
            }
            Napier.d(tag = "MainViewModel") { "after loadData()" }
            _isLoading.update { false }
            Napier.d(tag = "MainViewModel") { "_isLoading == ${_isLoading.value}" }
        }
    }
}