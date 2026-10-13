/*
 * Copyright (C) Anton Liaukevich 2021-2022 <leva.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.saintleva.sourcechew.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.Range
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.clear_filter
import sourcechew.composeapp.generated.resources.filter_from
import sourcechew.composeapp.generated.resources.filter_to
import sourcechew.composeapp.generated.resources.range_error_order

enum class IntRangeMode(val symbol: String) {
    EXACT("="),
    AT_LEAST(">="),
    AT_MOST("<="),
    BETWEEN("..")
}

/**
 * A composable widget for editing an optional numeric [Range].
 *
 * Supports four modes: exact (=), at least (>=), at most (<=), and between (..).
 *
 * @param label Label for the filter.
 * @param filter Current optional range filter state.
 * @param onFilterChange Callback triggered when the range is modified or toggled.
 * @param modifier Layout modifier.
 * @param enabled Whether user interaction is enabled.
 * @param defaultValue Default numeric value when filter is newly enabled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntRangeInput(
    label: String,
    filter: Range<Int>?,
    onFilterChange: (Range<Int>?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultValue: Int = 1
) {
    val isEnabled = filter != null

    val currentMode = remember(filter) {
        when {
            filter == null -> IntRangeMode.AT_LEAST
            filter.isExact -> IntRangeMode.EXACT
            filter.from != null && filter.to != null -> IntRangeMode.BETWEEN
            filter.from != null -> IntRangeMode.AT_LEAST
            else -> IntRangeMode.AT_MOST
        }
    }

    var selectedMode by remember(isEnabled) { mutableStateOf(currentMode) }

    LaunchedEffect(currentMode) {
        selectedMode = currentMode
    }

    var fromText by remember(filter?.from) { mutableStateOf(filter?.from?.toString() ?: "") }
    var toText by remember(filter?.to) { mutableStateOf(filter?.to?.toString() ?: "") }

    val fromInt = fromText.toIntOrNull()
    val toInt = toText.toIntOrNull()
    val hasOrderError = selectedMode == IntRangeMode.BETWEEN && fromInt != null && toInt != null && fromInt > toInt

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
                        onFilterChange(Range.atLeast(defaultValue))
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
                        IntRangeMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.symbol) },
                                onClick = {
                                    menuExpanded = false
                                    selectedMode = mode
                                    val currentVal = filter.from ?: filter.to ?: defaultValue
                                    when (mode) {
                                        IntRangeMode.EXACT -> onFilterChange(Range.exact(currentVal))
                                        IntRangeMode.AT_LEAST -> onFilterChange(Range.atLeast(currentVal))
                                        IntRangeMode.AT_MOST -> onFilterChange(Range.atMost(currentVal))
                                        IntRangeMode.BETWEEN -> {
                                            val endVal = filter.to?.takeIf { it >= currentVal } ?: (currentVal + 10)
                                            onFilterChange(Range.between(currentVal, endVal))
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                if (selectedMode == IntRangeMode.BETWEEN) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = fromText,
                            onValueChange = { text ->
                                val clean = text.filter { it.isDigit() }
                                fromText = clean
                                val newFrom = clean.toIntOrNull()
                                if (newFrom != null && toInt != null && newFrom <= toInt) {
                                    onFilterChange(Range(from = newFrom, to = toInt))
                                } else if (newFrom != null && toInt == null) {
                                    onFilterChange(Range.atLeast(newFrom))
                                } else if (newFrom == null && toInt != null) {
                                    onFilterChange(Range.atMost(toInt))
                                }
                            },
                            label = { Text(stringResource(Res.string.filter_from)) },
                            enabled = enabled,
                            isError = hasOrderError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = toText,
                            onValueChange = { text ->
                                val clean = text.filter { it.isDigit() }
                                toText = clean
                                val newTo = clean.toIntOrNull()
                                if (fromInt != null && newTo != null && fromInt <= newTo) {
                                    onFilterChange(Range(from = fromInt, to = newTo))
                                } else if (fromInt == null && newTo != null) {
                                    onFilterChange(Range.atMost(newTo))
                                } else if (fromInt != null && newTo == null) {
                                    onFilterChange(Range.atLeast(fromInt))
                                }
                            },
                            label = { Text(stringResource(Res.string.filter_to)) },
                            enabled = enabled,
                            isError = hasOrderError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = { onFilterChange(null) },
                                    enabled = enabled
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(Res.string.clear_filter)
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    val singleValueText = when (selectedMode) {
                        IntRangeMode.EXACT -> filter.from?.toString() ?: ""
                        IntRangeMode.AT_LEAST -> filter.from?.toString() ?: ""
                        IntRangeMode.AT_MOST -> filter.to?.toString() ?: ""
                        IntRangeMode.BETWEEN -> ""
                    }
                    var textValue by remember(singleValueText) { mutableStateOf(singleValueText) }

                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { text ->
                            val clean = text.filter { it.isDigit() }
                            textValue = clean
                            val num = clean.toIntOrNull()
                            if (num != null && num > 0) {
                                when (selectedMode) {
                                    IntRangeMode.EXACT -> onFilterChange(Range.exact(num))
                                    IntRangeMode.AT_LEAST -> onFilterChange(Range.atLeast(num))
                                    IntRangeMode.AT_MOST -> onFilterChange(Range.atMost(num))
                                    IntRangeMode.BETWEEN -> {}
                                }
                            }
                        },
                        label = { Text(label) },
                        enabled = enabled,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { onFilterChange(null) },
                                enabled = enabled
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(Res.string.clear_filter)
                                )
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
