package com.github.saintleva.sourcechew.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.paginationPageSizeRange
import com.github.saintleva.sourcechew.ui.common.NumberSpinEdit
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.decrease
import sourcechew.composeapp.generated.resources.increase
import sourcechew.composeapp.generated.resources.pagination_page_size
import sourcechew.composeapp.generated.resources.pagination_page_size_hint
import sourcechew.composeapp.generated.resources.pagination_page_size_range
import sourcechew.composeapp.generated.resources.pagination_section


@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}
@Composable
fun SettingsScreen(
    modifier: Modifier,
    viewModel: SettingsViewModel
) {
    val pageSize by viewModel.pageSize.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsSection(title = stringResource(Res.string.pagination_section)) {
            Column {
                NumberSpinEdit(
                    label = stringResource(Res.string.pagination_page_size),
                    rangeLabel = stringResource(
                        Res.string.pagination_page_size_range,
                        paginationPageSizeRange.first,
                        paginationPageSizeRange.last
                    ),
                    value = pageSize,
                    onValueChange = viewModel::onPageSizeChange,
                    range = paginationPageSizeRange,
                    decreaseContentDescription = stringResource(Res.string.decrease),
                    increaseContentDescription = stringResource(Res.string.increase)
                )
                Text(
                    text = stringResource(Res.string.pagination_page_size_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )
            }
        }
    }
}
