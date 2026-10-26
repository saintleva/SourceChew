package com.github.saintleva.sourcechew.ui.common

import androidx.compose.ui.graphics.Color

fun getLanguageColor(name: String): Color {
    return when (name.lowercase()) {
        "kotlin" -> Color(0xFFA97BFF)
        "java" -> Color(0xFFB07219)
        "swift" -> Color(0xFFF05138)
        "python" -> Color(0xFF3572A5)
        "javascript", "js" -> Color(0xFFF1E05A)
        "typescript", "ts" -> Color(0xFF3178C6)
        "html" -> Color(0xFFE34C26)
        "css" -> Color(0xFF563D7C)
        "c++" -> Color(0xFFF34B7D)
        "c" -> Color(0xFF555555)
        "c#" -> Color(0xFF178600)
        "go" -> Color(0xFF00ADD8)
        "rust" -> Color(0xFFDEA584)
        "ruby" -> Color(0xFF701516)
        "php" -> Color(0xFF4F5D95)
        "shell" -> Color(0xFF89E051)
        "objective-c" -> Color(0xFF438EFF)
        "dart" -> Color(0xFF00B4AB)
        "scala" -> Color(0xFFDC322F)
        else -> {
            val hash = name.hashCode()
            val hue = (hash and 0x7FFFFFFF) % 360f
            Color.hsl(hue, 0.65f, 0.55f)
        }
    }
}

fun formatPercentage(percentage: Float): String {
    return if (percentage < 0.1f && percentage > 0f) {
        "< 0.1%"
    } else {
        val rounded = (percentage * 10).toInt() / 10f
        if (rounded % 1f == 0f) {
            "${rounded.toInt()}%"
        } else {
            "$rounded%"
        }
    }
}
