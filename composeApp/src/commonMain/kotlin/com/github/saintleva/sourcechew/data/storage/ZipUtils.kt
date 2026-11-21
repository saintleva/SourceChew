package com.github.saintleva.sourcechew.data.storage

import okio.FileSystem
import okio.Path

expect fun FileSystem.openZipFileSystem(zipPath: Path): FileSystem
