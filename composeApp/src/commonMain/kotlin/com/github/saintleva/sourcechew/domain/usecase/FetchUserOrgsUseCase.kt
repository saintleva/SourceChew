package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.Owner
import com.jamal_aliev.paginator.offset.Paginator

interface FetchUserOrgsUseCase {
    suspend operator fun invoke(username: String): Paginator<Owner>
}
