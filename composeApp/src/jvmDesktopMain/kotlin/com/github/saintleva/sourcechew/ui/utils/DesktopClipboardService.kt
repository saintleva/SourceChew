package com.github.saintleva.sourcechew.ui.utils

import androidx.compose.ui.text.AnnotatedString
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

class DesktopClipboardService : ClipboardService {
    override suspend fun copy(text: String) {
        val selection = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }

    override suspend fun copy(annotatedString: AnnotatedString) {
        copy(annotatedString.text)
    }

    override suspend fun read(): String? {
        return try {
            val contents = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                contents.getTransferData(DataFlavor.stringFlavor) as String
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
