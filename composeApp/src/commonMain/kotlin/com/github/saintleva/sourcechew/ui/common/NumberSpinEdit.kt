package com.github.saintleva.sourcechew.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


/**
 * Custom implementation of the 'Remove' (minus) icon
 * to avoid importing the heavy material-icons-extended library.
 */
private val IconRemove: ImageVector
    get() = ImageVector.Builder(
        name = "Remove",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19f, 13f)
            horizontalLineTo(5f)
            verticalLineTo(11f)
            horizontalLineTo(19f)
            verticalLineTo(13f)
            close()
        }
    }.build()

@Composable
fun NumberSpinEdit(
    label: String,
    rangeLabel: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    decreaseContentDescription: String,
    increaseContentDescription: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Label and Range hint section
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = rangeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Control section (buttons and input field)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            IconButton(
                onClick = { onValueChange(value - 1) },
                enabled = value > range.first
            ) {
                Icon(
                    imageVector = IconRemove,
                    contentDescription = decreaseContentDescription,
                    tint = if (value > range.first)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            }

            BasicTextField(
                value = value.toString(),
                onValueChange = { s ->
                    // Allow only numeric input
                    val filtered = s.filter { it.isDigit() }
                    if (filtered.isNotEmpty()) {
                        val newValue = filtered.toIntOrNull()
                        if (newValue != null) {
                            // Clamp value within the allowed range
                            onValueChange(newValue.coerceIn(range))
                        }
                    }
                },
                modifier = Modifier.width(44.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            IconButton(
                onClick = { onValueChange(value + 1) },
                enabled = value < range.last
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = increaseContentDescription,
                    tint = if (value < range.last)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}