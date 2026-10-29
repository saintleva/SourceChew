package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.jamal_aliev.paginator.offset.Paginator


interface FetchFoundItemsUseCase<ItemSearchConditions, out FoundItem: FoundBase> {
    suspend operator fun invoke(conditions: ItemSearchConditions): Paginator<@UnsafeVariance FoundItem>
}
