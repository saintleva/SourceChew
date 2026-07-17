package com.github.saintleva.sourcechew.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.BaseSearchConditions
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.models.SearchOrder
import com.github.saintleva.sourcechew.domain.usecase.SearchState
import com.github.saintleva.sourcechew.ui.common.CheckBoxWithText
import com.github.saintleva.sourcechew.ui.common.ExpandableSection
import com.github.saintleva.sourcechew.ui.common.RadioButtonWithText
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.ascending
import sourcechew.composeapp.generated.resources.descending
import sourcechew.composeapp.generated.resources.enter_search_text
import sourcechew.composeapp.generated.resources.order
import sourcechew.composeapp.generated.resources.search
import sourcechew.composeapp.generated.resources.stop_search
import sourcechew.composeapp.generated.resources.use_previous_search_conditions


@Composable
fun <SearchConditions : BaseSearchConditions<SearchConditions>, FoundItem : FoundBase> BaseSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: BaseSearchViewModel<SearchConditions, FoundItem>,
    onFound: () -> Unit,
    specificFilters: @Composable (conditions: SearchConditions, selectingEnabled: Boolean) -> Unit
) {
    val searchState = viewModel.searchState.collectAsStateWithLifecycle()

    LaunchedEffect(searchState.value) {
        if (searchState.value is SearchState.Found) {
            onFound()
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight
        val density = LocalDensity.current
        var contentHeightDp by remember { mutableStateOf(0.dp) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        contentHeightDp = with(density) { coordinates.size.height.toDp() }
                    }
            ) {
                BaseSearchContent(
                    viewModel = viewModel,
                    selectingEnabled = searchState.value != SearchState.Searching,
                    specificFilters = specificFilters
                )
            }

            if (searchState.value == SearchState.Searching) {
                val remainingHeight = screenHeight - contentHeightDp

                SearchProgress(
                    onStop = viewModel::stop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = if (remainingHeight > 0.dp) remainingHeight else 0.dp)
                )
            }
        }
    }
}

@Composable
private fun <SearchConditions : BaseSearchConditions<SearchConditions>, FoundItem : FoundBase> BaseSearchContent(
    viewModel: BaseSearchViewModel<SearchConditions, FoundItem>,
    selectingEnabled: Boolean,
    specificFilters: @Composable (conditions: SearchConditions, selectingEnabled: Boolean) -> Unit
) {
    val conditions by viewModel.conditions.collectAsStateWithLifecycle()
    val usePreviousSearch by viewModel.usePreviousSearch.collectAsStateWithLifecycle()
    val maySearch by viewModel.maySearch.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = conditions.common.query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            enabled = selectingEnabled,
            textStyle = TextStyle(fontSize = 16.sp),
            label = { Text(stringResource(Res.string.enter_search_text)) },
            isError = conditions.common.query.isBlank()
        )

        specificFilters(conditions, selectingEnabled)

        ExpandableSection(title = stringResource(Res.string.order)) {
            SearchOrder.entries.forEach { order ->
                RadioButtonWithText(
                    text = order.displayText(),
                    selected = conditions.common.order == order,
                    onClick = { viewModel.onOrderChange(order) },
                    enabled = selectingEnabled,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        CheckBoxWithText(
            text = stringResource(Res.string.use_previous_search_conditions),
            checked = usePreviousSearch,
            onCheckedChange = viewModel::usePreviousSearchChange,
            enabled = selectingEnabled && viewModel.canUsePreviousConditions(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Button(
            onClick = viewModel::search,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            enabled = selectingEnabled && maySearch
        ) {
            Text(stringResource(Res.string.search))
        }
    }
}

@Composable
private fun SearchProgress(
    onStop: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        Button(
            onClick = onStop,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(stringResource(Res.string.stop_search))
        }
    }
}

@Composable
private fun SearchOrder.displayText(): String = when (this) {
    SearchOrder.ASCENDING -> stringResource(Res.string.ascending)
    SearchOrder.DESCENDING -> stringResource(Res.string.descending)
}