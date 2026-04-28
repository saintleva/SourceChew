package com.github.saintleva.sourcechew.ui.utils

import androidx.compose.ui.text.AnnotatedString
import io.github.aakira.napier.Napier
import kotlinx.coroutines.await
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.Promise

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => window.navigator.clipboard.readText()")
external fun jsReadText(): Promise<JsString>

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(text) => window.navigator.clipboard.writeText(text)")
external fun jsWriteText(text: String): Promise<JsAny?>

class WasmJsClipboardService : ClipboardService {
    override suspend fun copy(text: String) {
        try {
            jsWriteText(text).await<JsAny?>()
        } catch (e: Exception) {
            Napier.e(tag = "WasmJsClipboardService", throwable = e) { "Copy failed" }
        }
    }

    override suspend fun copy(annotatedString: AnnotatedString) {
        copy(annotatedString.text)
    }

    override suspend fun read(): String? {
        Napier.d(tag = "WasmJsClipboardService") { "Reading clipboard..." }
        return try {
            val text = jsReadText().await<JsString>().toString()
            Napier.d(tag = "WasmJsClipboardService") { "Read success: $text" }
            text
        } catch (e: Exception) {
            Napier.e(tag = "WasmJsClipboardService", throwable = e) { "Read failed" }
            null
        }
    }
}
