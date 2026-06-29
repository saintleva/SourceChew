package com.github.saintleva.sourcechew.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.CommonFilters
import com.github.saintleva.sourcechew.domain.models.HasCommonFilters
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


//TODO: Use or remove this
//fun <C : HasCommonFilters<C>> ViewModel.updateCommonFilters(
//    store: ConfigStore<C>,
//    transform: (CommonFilters) -> CommonFilters
//) {
//    viewModelScope.launch {
//        store.update { current ->
//            current.withCommon(transform(current.common))
//        }
//    }
//}