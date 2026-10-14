package com.github.saintleva.sourcechew.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.Range
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.clear_filter
import sourcechew.composeapp.generated.resources.date_error_format
import sourcechew.composeapp.generated.resources.dialog_cancel
import sourcechew.composeapp.generated.resources.dialog_ok
import sourcechew.composeapp.generated.resources.filter_from
import sourcechew.composeapp.generated.resources.filter_to
import sourcechew.composeapp.generated.resources.range_error_order
import sourcechew.composeapp.generated.resources.select_date
import sourcechew.composeapp.generated.resources.select_date_range

enum class DateRangeMode(val symbol: String) {
    EXACT("="),
    AT_LEAST(">="),
    AT_MOST("<="),
    BETWEEN("..")
}

private fun Long.toLocalDateUtc(): LocalDate {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
}

private fun LocalDate.toEpochMillisecondsUtc(): Long {
    return atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}

/**
 * A composable widget for editing an optional date [Range].
 *
 * Supports four modes: exact (=), at least (>=), at most (<=), and between (..).
 * Integrates Material 3 [DatePicker] and [DateRangePicker] modal dialogs.
 *
 * @param label Label for the filter.
 * @param filter Current optional range filter state.
 * @param onFilterChange Callback triggered when the range is modified or toggled.
 * @param modifier Layout modifier.
 * @param enabled Whether user interaction is enabled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeInput(
    label: String,
    filter: Range<LocalDate>?,
    onFilterChange: (Range<LocalDate>?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val isEnabled = filter != null

    val currentMode = remember(filter) {
        when {
            filter == null -> DateRangeMode.AT_LEAST
            filter.isExact -> DateRangeMode.EXACT
            (filter.from != null && filter.to != null) -> DateRangeMode.BETWEEN
            filter.from != null -> DateRangeMode.AT_LEAST
            else -> DateRangeMode.AT_MOST
        }
    }

    var selectedMode by remember(isEnabled) { mutableStateOf(currentMode) }

    LaunchedEffect(currentMode) {
        selectedMode = currentMode
    }

    var fromText by remember(filter?.from) { mutableStateOf(filter?.from?.toString() ?: "") }
    var toText by remember(filter?.to) { mutableStateOf(filter?.to?.toString() ?: "") }

    val fromDate = runCatching { LocalDate.parse(fromText) }.getOrNull()
    val toDate = runCatching { LocalDate.parse(toText) }.getOrNull()

    val isFromFormatError = fromText.isNotBlank() && fromDate == null
    val isToFormatError = toText.isNotBlank() && toDate == null
    val hasOrderError = selectedMode == DateRangeMode.BETWEEN && fromDate != null && toDate != null && fromDate > toDate

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showDateRangePickerDialog by remember { mutableStateOf(false) }

    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Switch(
                checked = isEnabled,
                onCheckedChange = { checked ->
                    if (checked) {
                        onFilterChange(Range.atLeast(today))
                    } else {
                        onFilterChange(null)
                    }
                },
                enabled = enabled
            )

            if (filter != null) {
                var menuExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = menuExpanded,
                    onExpandedChange = { if (enabled) menuExpanded = !menuExpanded },
                    modifier = Modifier.width(95.dp)
                ) {
                    OutlinedTextField(
                        value = selectedMode.symbol,
                        onValueChange = {},
                        readOnly = true,
                        enabled = enabled,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DateRangeMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.symbol) },
                                onClick = {
                                    menuExpanded = false
                                    selectedMode = mode
                                    val currentVal = filter.from ?: filter.to ?: today
                                    when (mode) {
                                        DateRangeMode.EXACT -> onFilterChange(Range.exact(currentVal))
                                        DateRangeMode.AT_LEAST -> onFilterChange(Range.atLeast(currentVal))
                                        DateRangeMode.AT_MOST -> onFilterChange(Range.atMost(currentVal))
                                        DateRangeMode.BETWEEN -> {
                                            val endVal = filter.to?.takeIf { it >= currentVal } ?: currentVal
                                            onFilterChange(Range.between(currentVal, endVal))
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                if (selectedMode == DateRangeMode.BETWEEN) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = fromText,
                            onValueChange = { text ->
                                fromText = text.trim()
                                val newFrom = runCatching { LocalDate.parse(fromText) }.getOrNull()
                                if (newFrom != null && toDate != null && newFrom <= toDate) {
                                    onFilterChange(Range(from = newFrom, to = toDate))
                                } else if (newFrom != null && toDate == null) {
                                    onFilterChange(Range.atLeast(newFrom))
                                } else if (newFrom == null && toDate != null) {
                                    onFilterChange(Range.atMost(toDate))
                                }
                            },
                            label = { Text(stringResource(Res.string.filter_from)) },
                            enabled = enabled,
                            isError = isFromFormatError || hasOrderError,
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = toText,
                            onValueChange = { text ->
                                toText = text.trim()
                                val newTo = runCatching { LocalDate.parse(toText) }.getOrNull()
                                if (fromDate != null && newTo != null && fromDate <= newTo) {
                                    onFilterChange(Range(from = fromDate, to = newTo))
                                } else if (fromDate == null && newTo != null) {
                                    onFilterChange(Range.atMost(newTo))
                                } else if (fromDate != null && newTo == null) {
                                    onFilterChange(Range.atLeast(fromDate))
                                }
                            },
                            label = { Text(stringResource(Res.string.filter_to)) },
                            enabled = enabled,
                            isError = isToFormatError || hasOrderError,
                            singleLine = true,
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { showDateRangePickerDialog = true },
                                        enabled = enabled
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = stringResource(Res.string.select_date_range)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onFilterChange(null) },
                                        enabled = enabled
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = stringResource(Res.string.clear_filter)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                } else {
                    val singleValueText = when (selectedMode) {
                        DateRangeMode.EXACT -> filter.from?.toString() ?: ""
                        DateRangeMode.AT_LEAST -> filter.from?.toString() ?: ""
                        DateRangeMode.AT_MOST -> filter.to?.toString() ?: ""
                        DateRangeMode.BETWEEN -> ""
                    }
                    var textValue by remember(singleValueText) { mutableStateOf(singleValueText) }

                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { text ->
                            textValue = text.trim()
                            val parsed = runCatching { LocalDate.parse(textValue) }.getOrNull()
                            if (parsed != null) {
                                when (selectedMode) {
                                    DateRangeMode.EXACT -> onFilterChange(Range.exact(parsed))
                                    DateRangeMode.AT_LEAST -> onFilterChange(Range.atLeast(parsed))
                                    DateRangeMode.AT_MOST -> onFilterChange(Range.atMost(parsed))
                                    DateRangeMode.BETWEEN -> {}
                                }
                            }
                        },
                        label = { Text(label) },
                        enabled = enabled,
                        isError = textValue.isNotBlank() && runCatching { LocalDate.parse(textValue) }.isFailure,
                        singleLine = true,
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { showDatePickerDialog = true },
                                    enabled = enabled
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = stringResource(Res.string.select_date)
                                    )
                                }
                                IconButton(
                                    onClick = { onFilterChange(null) },
                                    enabled = enabled
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(Res.string.clear_filter)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = label,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (hasOrderError) {
            Text(
                text = stringResource(Res.string.range_error_order),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 2.dp)
            )
        } else if (isFromFormatError || isToFormatError) {
            Text(
                text = stringResource(Res.string.date_error_format),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 2.dp)
            )
        }
    }

    if (showDatePickerDialog) {
        val initialMillis = when (selectedMode) {
            DateRangeMode.EXACT, DateRangeMode.AT_LEAST -> filter?.from?.toEpochMillisecondsUtc()
            DateRangeMode.AT_MOST -> filter?.to?.toEpochMillisecondsUtc()
            DateRangeMode.BETWEEN -> null
        } ?: today.toEpochMillisecondsUtc()

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val selectedDate = millis.toLocalDateUtc()
                            when (selectedMode) {
                                DateRangeMode.EXACT -> onFilterChange(Range.exact(selectedDate))
                                DateRangeMode.AT_LEAST -> onFilterChange(Range.atLeast(selectedDate))
                                DateRangeMode.AT_MOST -> onFilterChange(Range.atMost(selectedDate))
                                DateRangeMode.BETWEEN -> {}
                            }
                        }
                    }
                ) {
                    Text(stringResource(Res.string.dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text(stringResource(Res.string.dialog_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDateRangePickerDialog) {
        val initialStartMillis = filter?.from?.toEpochMillisecondsUtc() ?: today.toEpochMillisecondsUtc()
        val initialEndMillis = filter?.to?.toEpochMillisecondsUtc()

        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = initialStartMillis,
            initialSelectedEndDateMillis = initialEndMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDateRangePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDateRangePickerDialog = false
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis
                        if (startMillis != null && endMillis != null) {
                            val startDate = startMillis.toLocalDateUtc()
                            val endDate = endMillis.toLocalDateUtc()
                            onFilterChange(Range.between(startDate, endDate))
                        } else if (startMillis != null) {
                            onFilterChange(Range.atLeast(startMillis.toLocalDateUtc()))
                        } else if (endMillis != null) {
                            onFilterChange(Range.atMost(endMillis.toLocalDateUtc()))
                        }
                    }
                ) {
                    Text(stringResource(Res.string.dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePickerDialog = false }) {
                    Text(stringResource(Res.string.dialog_cancel))
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(16.dp)
            )
        }
    }
}
