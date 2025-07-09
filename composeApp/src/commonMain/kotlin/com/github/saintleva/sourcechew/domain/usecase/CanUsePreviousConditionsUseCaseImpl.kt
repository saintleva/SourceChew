package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigRepository
import com.github.saintleva.sourcechew.domain.repository.SearchRepository
import kotlinx.coroutines.flow.first


class CanUsePreviousConditionsUseCaseImpl(
    private val configRepository: ConfigRepository,
    private val searchRepository: SearchRepository
) : CanUsePreviousConditionsUseCase {

    override suspend fun invoke(newConditions: RepoSearchConditions): Boolean {
        return searchRepository.everSearched &&
                (newConditions == configRepository.previousRepoConditions.first())
    }
}