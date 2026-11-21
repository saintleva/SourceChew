package com.github.saintleva.sourcechew.data.storage

import okio.FileSystem
import okio.Path
import okio.openZip

actual fun FileSystem.openZipFileSystem(zipPath: Path): FileSystem = this.openZip(zipPath)
