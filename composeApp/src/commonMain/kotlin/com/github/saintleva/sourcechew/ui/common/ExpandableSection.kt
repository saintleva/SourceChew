package com.github.saintleva.sourcechew.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp


@Composable
fun ExpandableSection(
    modifier: Modifier = Modifier,
    title: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    initiallyExpanded: Boolean = false,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var internalExpanded by rememberSaveable(title) { mutableStateOf(initiallyExpanded) }
    val currentExpanded = expanded ?: internalExpanded

    Column(modifier = modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(title) },
            leadingContent = leadingIcon,
            trailingContent = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = if (currentExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(if (currentExpanded) 180f else 0f)
                )
            },
            modifier = Modifier.clickable {
                val next = !currentExpanded
                if (onExpandedChange != null) {
                    onExpandedChange(next)
                } else {
                    internalExpanded = next
                }
            }
        )

        AnimatedVisibility(visible = currentExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp),
                content = content
            )
        }
    }
}