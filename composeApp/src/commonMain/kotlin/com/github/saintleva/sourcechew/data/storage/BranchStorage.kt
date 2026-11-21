package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.domain.models.RepoRef
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

interface BranchStorage {
    /**
     * Returns a temporary ZIP file path for downloading a branch archive.
     */
    fun getTempZipPath(repoRef: RepoRef, branch: String): Path

    /**
     * Returns the target directory path where extracted branch source files are stored.
     */
    fun getTargetDirectoryPath(repoRef: RepoRef, branch: String): Path

    /**
     * Extracts the specified ZIP archive into the target directory, stripping top-level archive prefix.
     * Cleans up existing files in target directory prior to extraction.
     */
    suspend fun extractZipArchive(zipPath: Path, targetDirPath: Path): String

    /**
     * Safely deletes a file or directory if it exists.
     */
    suspend fun deleteIfExists(path: Path)
}

class OkioBranchStorage(
    private val baseDir: Path,
    private val fileSystem: FileSystem
) : BranchStorage {

    override fun getTempZipPath(repoRef: RepoRef, branch: String): Path {
        val sanitizedBranch = sanitizeName(branch)
        return baseDir / repoRef.owner / repoRef.name / "$sanitizedBranch.zip.tmp"
    }

    override fun getTargetDirectoryPath(repoRef: RepoRef, branch: String): Path {
        val sanitizedBranch = sanitizeName(branch)
        return baseDir / repoRef.owner / repoRef.name / sanitizedBranch
    }

    override suspend fun deleteIfExists(path: Path) {
        if (fileSystem.exists(path)) {
            fileSystem.deleteRecursively(path)
        }
    }

    override suspend fun extractZipArchive(zipPath: Path, targetDirPath: Path): String {
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

            for (entryPath in allPaths) {
                if (entryPath == rootPath || (topPrefix != null && entryPath == topPrefix)) continue

                val relativePath = if (topPrefix != null && entryPath.toString().startsWith(topPrefix.toString())) {
                    entryPath.relativeTo(topPrefix)
                } else {
                    entryPath.relativeTo(rootPath)
                }

                if (relativePath.toString().isEmpty() || relativePath.toString() == ".") continue

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
