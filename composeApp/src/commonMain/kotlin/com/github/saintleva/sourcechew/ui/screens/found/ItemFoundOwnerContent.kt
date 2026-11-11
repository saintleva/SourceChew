package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.runtime.Composable
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.ui.common.OwnerItemCard

@Composable
fun ItemFoundOwnerContent(
    owner: FoundOwner,
    onClick: () -> Unit
) {
    OwnerItemCard(
        login = owner.login,
        avatarUrl = owner.avatarUrl,
        type = owner.type,
        onClick = onClick
    )
}
