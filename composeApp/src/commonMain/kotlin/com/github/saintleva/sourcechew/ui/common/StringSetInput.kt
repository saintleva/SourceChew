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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.add
import sourcechew.composeapp.generated.resources.clear_filter
import sourcechew.composeapp.generated.resources.enter_value_hint
import sourcechew.composeapp.generated.resources.remove

@Composable
fun StringSetInput(
    label: String,
    values: Set<String>,
    onValuesChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String? = null,
) {
    var text by remember { mutableStateOf("") }

    fun addTokens(input: String) {
        val tokens = input.split(",", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (tokens.isNotEmpty()) {
            onValuesChange(values + tokens)
        }
    }

    fun submitText() {
        if (text.isNotBlank()) {
            addTokens(text)
            text = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                if (newText.contains(",") || newText.contains("\n")) {
                    val parts = newText.split(",", "\n")
                    val toAdd = parts.dropLast(1).map { it.trim() }.filter { it.isNotEmpty() }
                    if (toAdd.isNotEmpty()) {
                        onValuesChange(values + toAdd)
                    }
                    text = parts.last()
                } else {
                    text = newText
                }
            },
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } } ?: { Text(stringResource(Res.string.enter_value_hint)) },
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submitText() }),
            trailingIcon = {
                if (text.isNotBlank()) {
                    IconButton(
                        onClick = { submitText() },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(Res.string.add)
                        )
                    }
                } else if (values.isNotEmpty()) {
                    IconButton(
                        onClick = { onValuesChange(emptySet()) },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.clear_filter)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (values.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                values.forEach { item ->
                    InputChip(
                        selected = false,
                        onClick = { if (enabled) onValuesChange(values - item) },
                        label = { Text(item) },
                        trailingIcon = if (enabled) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(Res.string.remove),
                                    modifier = Modifier.size(InputChipDefaults.AvatarSize)
                                )
                            }
                        } else null,
                        enabled = enabled
                    )
                }
            }
        }
    }
}
