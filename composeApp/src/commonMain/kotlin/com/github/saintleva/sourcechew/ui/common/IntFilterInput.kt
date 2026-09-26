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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.IntFilter
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.clear_filter

/**
 * Extension property providing a display symbol for each [IntFilter.Operator].
 */
val IntFilter.Operator.symbol: String
    get() = when (this) {
        IntFilter.Operator.EQ -> "="
        IntFilter.Operator.GT -> ">"
        IntFilter.Operator.GTE -> ">="
        IntFilter.Operator.LT -> "<"
        IntFilter.Operator.LTE -> "<="
    }

/**
 * A composable widget for editing an optional [IntFilter].
 *
 * Provides a toggle switch to enable/disable the filter, a dropdown selector
 * for the comparison operator, and an integer input field for the target value.
 *
 * @param label Label for the value text field.
 * @param filter Current optional filter state.
 * @param onFilterChange Callback triggered when filter is modified or toggled.
 * @param modifier Layout modifier.
 * @param enabled Whether user interaction is enabled.
 * @param defaultValue Default numeric value when filter is newly enabled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntFilterInput(
    label: String,
    filter: IntFilter?,
    onFilterChange: (IntFilter?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultValue: Int = 1
) {
    val isEnabled = filter != null

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Toggle switch to enable or disable the filter
        Switch(
            checked = isEnabled,
            onCheckedChange = { checked ->
                if (checked) {
                    onFilterChange(IntFilter(value = defaultValue, operator = IntFilter.Operator.GTE))
                } else {
                    onFilterChange(null)
                }
            },
            enabled = enabled
        )

        if (filter != null) {
            var expanded by remember { mutableStateOf(false) }

            // Dropdown menu for selecting comparison operator
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (enabled) expanded = !expanded },
                modifier = Modifier.width(90.dp)
            ) {
                OutlinedTextField(
                    value = filter.operator.symbol,
                    onValueChange = {},
                    readOnly = true,
                    enabled = enabled,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    IntFilter.Operator.entries.forEach { op ->
                        DropdownMenuItem(
                            text = { Text(op.symbol) },
                            onClick = {
                                expanded = false
                                onFilterChange(filter.copy(operator = op))
                            }
                        )
                    }
                }
            }

            // Numeric input field for filter value (value must be positive)
            OutlinedTextField(
                value = filter.value.toString(),
                onValueChange = { text ->
                    val newInt = text.filter { it.isDigit() }.toIntOrNull()
                    if (newInt != null && newInt > 0) {
                        onFilterChange(filter.copy(value = newInt))
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
        } else {
            // Label shown when filter is currently disabled
            Text(
                text = label,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
