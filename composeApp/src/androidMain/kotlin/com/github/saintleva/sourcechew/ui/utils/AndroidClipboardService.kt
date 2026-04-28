package com.github.saintleva.sourcechew.ui.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.text.AnnotatedString


class AndroidClipboardService(private val context: Context) : ClipboardService {
    private val clipboardManager by lazy {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override suspend fun copy(text: String) {
        val clip = ClipData.newPlainText("SourceChew", text)
        clipboardManager.setPrimaryClip(clip)
    }

    override suspend fun copy(annotatedString: AnnotatedString) {
        copy(annotatedString.text)
    }

    override suspend fun read(): String? {
        val clip = clipboardManager.primaryClip
        if (clip != null && clip.itemCount > 0) {
            return clip.getItemAt(0).text?.toString()
        }
        return null
    }
}
