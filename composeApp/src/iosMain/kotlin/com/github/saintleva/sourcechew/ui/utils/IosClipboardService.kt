package com.github.saintleva.sourcechew.ui.utils

import androidx.compose.ui.text.AnnotatedString
import platform.UIKit.UIPasteboard

class IosClipboardService : ClipboardService {
    override suspend fun copy(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    override suspend fun copy(annotatedString: AnnotatedString) {
        copy(annotatedString.text)
    }

    override suspend fun read(): String? {
        return UIPasteboard.generalPasteboard.string
    }
}
