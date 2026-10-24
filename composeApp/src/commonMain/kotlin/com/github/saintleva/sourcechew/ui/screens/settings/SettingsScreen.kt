package com.github.saintleva.sourcechew.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.ui.common.NumberSpinEdit
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.decrease
import sourcechew.composeapp.generated.resources.increase
import sourcechew.composeapp.generated.resources.pagination_page_size
import sourcechew.composeapp.generated.resources.pagination_page_size_hint
import sourcechew.composeapp.generated.resources.pagination_page_size_range
import sourcechew.composeapp.generated.resources.pagination_section
import sourcechew.composeapp.generated.resources.description_section
import sourcechew.composeapp.generated.resources.max_repo_description_length
import sourcechew.composeapp.generated.resources.max_repo_description_length_hint
import sourcechew.composeapp.generated.resources.max_found_repo_description_length
import sourcechew.composeapp.generated.resources.max_found_repo_description_length_hint


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
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsSection(title = stringResource(Res.string.pagination_section)) {
            Column {
                val range = AppSettings.paginationPageSizeRange
                NumberSpinEdit(
                    label = stringResource(Res.string.pagination_page_size),
                    rangeLabel = stringResource(
                        Res.string.pagination_page_size_range,
                        range.first,
                        range.last
                    ),
                    value = settings.paginationPageSize,
                    onValueChange = viewModel::onPageSizeChange,
                    range = range,
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

        SettingsSection(title = stringResource(Res.string.description_section)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val repoDetailsRange = AppSettings.maxRepoDescriptionLengthRange
                Column {
                    NumberSpinEdit(
                        label = stringResource(Res.string.max_repo_description_length),
                        rangeLabel = stringResource(
                            Res.string.pagination_page_size_range,
                            repoDetailsRange.first,
                            repoDetailsRange.last
                        ),
                        value = settings.maxRepoDescriptionLength,
                        onValueChange = viewModel::onMaxRepoDescriptionLengthChange,
                        range = repoDetailsRange,
                        decreaseContentDescription = stringResource(Res.string.decrease),
                        increaseContentDescription = stringResource(Res.string.increase)
                    )
                    Text(
                        text = stringResource(Res.string.max_repo_description_length_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }

                val foundRepoRange = AppSettings.maxFoundRepoDescriptionLengthRange
                Column {
                    NumberSpinEdit(
                        label = stringResource(Res.string.max_found_repo_description_length),
                        rangeLabel = stringResource(
                            Res.string.pagination_page_size_range,
                            foundRepoRange.first,
                            foundRepoRange.last
                        ),
                        value = settings.maxFoundRepoDescriptionLength,
                        onValueChange = viewModel::onMaxFoundRepoDescriptionLengthChange,
                        range = foundRepoRange,
                        decreaseContentDescription = stringResource(Res.string.decrease),
                        increaseContentDescription = stringResource(Res.string.increase)
                    )
                    Text(
                        text = stringResource(Res.string.max_found_repo_description_length_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }
            }
        }
    }
}
