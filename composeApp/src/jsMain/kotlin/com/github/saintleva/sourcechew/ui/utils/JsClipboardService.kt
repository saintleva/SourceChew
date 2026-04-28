package com.github.saintleva.sourcechew.ui.utils

import androidx.compose.ui.text.AnnotatedString
import io.github.aakira.napier.Napier
import kotlinx.browser.window
import kotlinx.coroutines.await

class JsClipboardService : ClipboardService {
    override suspend fun copy(text: String) {
        try {
            window.navigator.clipboard.writeText(text).await()
        } catch (e: Exception) {
            Napier.e(tag = "JsClipboardService", throwable = e) { "Copy failed" }
        }
    }

    override suspend fun copy(annotatedString: AnnotatedString) {
        copy(annotatedString.text)
    }

    override suspend fun read(): String? {
        Napier.d(tag = "JsClipboardService") { "Reading clipboard..." }
        return try {
            val text = window.navigator.clipboard.readText().await()
            Napier.d(tag = "JsClipboardService") { "Read success: $text" }
            text
        } catch (e: Exception) {
            Napier.e(tag = "JsClipboardService", throwable = e) { "Read failed" }
            null
        }
    }
}
