package com.github.saintleva.sourcechew.ui.screens.members

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.ui.common.OwnerItemCard
import com.github.saintleva.sourcechew.ui.common.PaginatedList
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.members

@Composable
fun MembersScreen(
    modifier: Modifier = Modifier,
    viewModel: MembersViewModel,
    onMemberClick: (Owner) -> Unit = {}
) {
    val paginator by viewModel.paginatorState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(Res.string.members),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        PaginatedList(
            viewModel = viewModel,
            paginator = paginator,
            modifier = Modifier.fillMaxSize(),
            key = { it.id }
        ) { member, _, _, _, _ ->
            OwnerItemCard(
                login = member.login,
                avatarUrl = member.avatarUrl,
                onClick = { onMemberClick(member) }
            )
        }
    }
}
