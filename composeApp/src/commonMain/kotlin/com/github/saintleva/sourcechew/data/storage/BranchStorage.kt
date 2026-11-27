package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.domain.models.BranchRef
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

interface BranchStorage {
    /**
     * Returns a temporary ZIP file path for downloading a branch archive.
     */
    fun getTempZipPath(branchRef: BranchRef): Path

    /**
     * Returns the target directory path where extracted branch source files are stored.
     */
    fun getTargetDirectoryPath(branchRef: BranchRef): Path

    /**
     * Extracts the specified ZIP archive into the target directory, stripping top-level archive prefix.
     * Cleans up existing files in target directory prior to extraction.
     * Invokes [onExtractProgress] periodically with the number of processed files and total entries.
     */
    suspend fun extractZipArchive(
        zipPath: Path,
        targetDirPath: Path,
        onExtractProgress: ((filesExtracted: Int, totalFiles: Int?) -> Unit)? = null
    ): String

    /**
     * Safely deletes a file or directory if it exists.
     */
    suspend fun deleteIfExists(path: Path)
}

class OkioBranchStorage(
    private val baseDir: Path,
    private val fileSystem: FileSystem
) : BranchStorage {

    override fun getTempZipPath(branchRef: BranchRef): Path {
        val sanitizedBranch = sanitizeName(branchRef.branch)
        return baseDir / branchRef.repoRef.owner / branchRef.repoRef.name / "$sanitizedBranch.zip.tmp"
    }

    override fun getTargetDirectoryPath(branchRef: BranchRef): Path {
        val sanitizedBranch = sanitizeName(branchRef.branch)
        return baseDir / branchRef.repoRef.owner / branchRef.repoRef.name / sanitizedBranch
    }

    override suspend fun deleteIfExists(path: Path) {
        if (fileSystem.exists(path)) {
            fileSystem.deleteRecursively(path)
        }
    }

    override suspend fun extractZipArchive(
        zipPath: Path,
        targetDirPath: Path,
        onExtractProgress: ((filesExtracted: Int, totalFiles: Int?) -> Unit)?
    ): String {
        // Ensure parent repo directory exists
        val parentDir = targetDirPath.parent
        if (parentDir != null && !fileSystem.exists(parentDir)) {
            fileSystem.createDirectories(parentDir)
        }

        // Clear existing target directory if present
        deleteIfExists(targetDirPath)
        fileSystem.createDirectories(targetDirPath)

        val zipFileSystem = fileSystem.openZipFileSystem(zipPath)
        try {
            val rootPath = "/".toPath()
            val rootEntries = zipFileSystem.list(rootPath)

            // GitHub archives usually contain a single top-level directory (e.g. owner-repo-sha/)
            val topPrefix = if (rootEntries.size == 1 && zipFileSystem.metadata(rootEntries.first()).isDirectory) {
                rootEntries.first()
            } else {
                null
            }

            val allPaths = zipFileSystem.listRecursively(rootPath).toList()
            val totalFiles = allPaths.size
            var extractedCount = 0

            onExtractProgress?.invoke(0, totalFiles)

            for (entryPath in allPaths) {
                extractedCount++
                if (entryPath == rootPath || (topPrefix != null && entryPath == topPrefix)) {
                    onExtractProgress?.invoke(extractedCount, totalFiles)
                    continue
                }

                val relativePath = if (topPrefix != null && entryPath.toString().startsWith(topPrefix.toString())) {
                    entryPath.relativeTo(topPrefix)
                } else {
                    entryPath.relativeTo(rootPath)
                }

                if (relativePath.toString().isEmpty() || relativePath.toString() == ".") {
                    onExtractProgress?.invoke(extractedCount, totalFiles)
                    continue
                }

                val destinationPath = targetDirPath / relativePath
                val isDirectory = zipFileSystem.metadata(entryPath).isDirectory

                if (isDirectory) {
                    fileSystem.createDirectories(destinationPath)
                } else {
                    val destParent = destinationPath.parent
                    if (destParent != null && !fileSystem.exists(destParent)) {
                        fileSystem.createDirectories(destParent)
                    }

                    zipFileSystem.source(entryPath).use { source ->
                        fileSystem.sink(destinationPath).buffer().use { sink ->
                            sink.writeAll(source)
                        }
                    }
                }
                onExtractProgress?.invoke(extractedCount, totalFiles)
            }
        } finally {
            zipFileSystem.close()
        }

        return targetDirPath.toString()
    }

    private fun sanitizeName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
}
