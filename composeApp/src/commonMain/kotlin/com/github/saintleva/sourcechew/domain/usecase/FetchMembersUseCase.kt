package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.Owner
import com.jamal_aliev.paginator.offset.Paginator

interface FetchMembersUseCase {
    suspend operator fun invoke(org: String): Paginator<Owner>
}
