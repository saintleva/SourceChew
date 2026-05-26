package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.jamal_aliev.paginator.offset.Paginator


interface GetReposUseCase {
    suspend operator fun invoke(conditions: RepoSearchConditions): Paginator<FoundRepo>
}
