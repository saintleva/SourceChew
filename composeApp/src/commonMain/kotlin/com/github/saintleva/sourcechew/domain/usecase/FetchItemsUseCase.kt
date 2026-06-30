package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.jamal_aliev.paginator.offset.Paginator


interface FetchItemsUseCase<ItemSearchConditions, out FoundItem: FoundBase> {
    suspend operator fun invoke(conditions: ItemSearchConditions): Paginator<out FoundItem>
}
