package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.repository.SearchRepository


class CanUsePreviousConditionsUseCaseImpl(
    private val configRepository: ConfigRepository,
    private val searchRepository: SearchRepository
) : CanUsePreviousConditionsUseCase {

    override fun invoke(newConditions: SearchConditions) =
        searchRepository.everSearched && (newConditions == configRepository.previousConditions)
}