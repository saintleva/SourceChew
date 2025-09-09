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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp


@Composable
fun BaseInteractiveRow(
    text: String,
    onRowClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: Role,
    controlContent: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                enabled = enabled,
                onClick = { if (enabled) onRowClick() },
                role = role
            )
    ) {
        controlContent()
        val textColor = if (enabled) LocalContentColor.current else
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun CheckBoxWithText(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    BaseInteractiveRow(
        text = text,
        onRowClick = { onCheckedChange(!checked) },
        modifier = modifier,
        enabled = enabled,
        role = Role.Checkbox
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled
        )
    }
}

@Composable
fun RadioButtonWithText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    BaseInteractiveRow(
        text = text,
        onRowClick = onClick,
        modifier = modifier,
        enabled = enabled,
        role = Role.RadioButton
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled
        )
    }
}

//TODO: Remove this
//interface CheckedChanging {
//    fun onClick(checked: Boolean)
//}
//
//@Composable
//fun SomethingWithText(
//    text: String,
//    checked: Boolean,
//    checkedChanging: CheckedChanging,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    role: Role,
//    something: @Composable () -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = modifier
//            .clip(MaterialTheme.shapes.small)
//            .clickable(
//                enabled = enabled,
//                onClick = { if (enabled) checkedChanging.onClick(checked) },
//                role = role
//            )
//    ) {
//        something()
//        val textColor = if (enabled) LocalContentColor.current else
//            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
//        Text(
//            text = text,
//            style = MaterialTheme.typography.bodyLarge,
//            color = textColor,
//            modifier = Modifier.padding(start = 8.dp)
//        )
//    }
//}
//
//@Composable
//fun CheckBoxWithText(
//    text: String,
//    checked: Boolean,
//    onCheckedChange: (Boolean) -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean
//) {
//    SomethingWithText(
//        text,
//        checked,
//        checkedChanging = object : CheckedChanging {
//            override fun onClick(checked: Boolean) {
//                onCheckedChange(!checked)
//            }
//        },
//        enabled = enabled,
//        modifier = modifier,
//        role = Role.Checkbox
//    ) {
//        Checkbox(
//            checked = checked,
//            onCheckedChange = { if (enabled) onCheckedChange(!checked) },
//            enabled = enabled
//        )
//    }
//}
//
//@Composable
//fun RadioButtonWithText(
//    text: String,
//    selected: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean
//) {
//    SomethingWithText(
//        text,
//        selected,
//        checkedChanging = object : CheckedChanging {
//            override fun onClick(checked: Boolean) {
//                onClick()
//            }
//        },
//        enabled = enabled,
//        modifier = modifier,
//        role = Role.RadioButton
//    ) {
//        RadioButton(
//            selected = selected,
//            onClick = { if (enabled) onClick() },
//            enabled = enabled
//        )
//    }
//}