package com.github.saintleva.sourcechew.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.ui.screens.PaginatedViewModel
import com.jamal_aliev.paginator.offset.Paginator

@Composable
fun OwnerListScreen(
    modifier: Modifier = Modifier,
    title: String,
    paginator: Paginator<Owner>?,
    viewModel: PaginatedViewModel<Owner>,
    onItemClick: (Owner) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        PaginatedList(
            viewModel = viewModel,
            paginator = paginator,
            modifier = Modifier.fillMaxSize(),
            key = { it.id }
        ) { item, _, _, _, _ ->
            OwnerItemCard(
                login = item.login,
                avatarUrl = item.avatarUrl,
                onClick = { onItemClick(item) }
            )
        }
    }
}
