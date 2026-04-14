package com.github.saintleva.sourcechew.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import sourcechew.composeapp.generated.resources.pagination_page_size_range


@Composable
fun SettingsScreen(
    modifier: Modifier,
    viewModel: SettingsViewModel
) {
    val pageSize by viewModel.pageSize.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
    }
}
