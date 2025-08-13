package com.github.saintleva.sourcechew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


class MainViewModel : ViewModel() {

    private val configRepository: ConfigRepository by inject(ConfigRepository::class.java)

    init {
        Napier.d(tag = "MainViewModel") { "Before data loading" }
        viewModelScope.launch {
            configRepository.loadData()
            Napier.d(tag = "MainViewModel") { "Launched data loading" }
        }
    }
}