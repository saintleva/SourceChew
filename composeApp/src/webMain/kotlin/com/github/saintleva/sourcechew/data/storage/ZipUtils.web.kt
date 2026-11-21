package com.github.saintleva.sourcechew.data.storage

import okio.FileSystem
import okio.Path

actual fun FileSystem.openZipFileSystem(zipPath: Path): FileSystem {
    throw UnsupportedOperationException("ZIP extraction is not supported on Web target")
}
