package com.github.saintleva.sourcechew.ui.utils

import androidx.compose.ui.text.AnnotatedString

interface ClipboardService {
    suspend fun copy(text: String)
    suspend fun copy(annotatedString: AnnotatedString)
    suspend fun read(): String?
}
