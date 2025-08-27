package com.github.saintleva.sourcechew.data.remote

import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import kotlin.random.Random
import kotlin.time.Duration

class MockSearchApiService(
    private val simulateError: Boolean = false,
    private val returnEmptyList: Boolean = false,
    private val eachCount: Int,
    private val delayImitation: Duration = Duration.ZERO,
) : SearchApiService {

    private companion object {
        val sampleDescriptions = listOf(
            "A cool Kotlin project for managing tasks.",
            "The official Android app for a popular news website.",
            "A library for creating beautiful UIs in Jetpack Compose.",
            "An example of clean architecture in a multiplatform app.",
            "A powerful tool for data analysis and visualization."
        )

        val sampleLanguages = listOf("Kotlin", "Java", "Swift", "Python", "JavaScript", "Go", "Rust")
        val sampleAuthors = listOf("SaintLeva", "JetBrains", "Google", "Square", "Microsoft", "JakeWharton")
    }

    override suspend fun searchItems(
        conditions: RepoSearchConditions,
        sort: String,
        order: String,
        page: Int,
        pageSize: Int
    ): SearchResponseDto {
        if (simulateError) {
            //TODO: Use every condition to simulate error
            throw Exception("Mock API Error: Failed to fetch search results for query: ${conditions.query}")
        }
        if (returnEmptyList) {
            return SearchResponseDto(totalCount = 0, items = emptyList())
        }

        val items = mutableListOf<FoundRepoDto>()
        val totalItemsToGenerate = if (page == 1) Random.nextInt(pageSize / 2, pageSize) else pageSize
        val totalCount = conditions.inScope.size * eachCount
        val startIndex = (page - 1) * pageSize
        if (startIndex >= totalCount) {
            return SearchResponseDto(totalCount = totalCount, items = emptyList())
        }

        val itemsOnThisPageCount = minOf(pageSize, totalCount - startIndex)

        for (i in 0 until itemsOnThisPageCount) {
            val uniqueId = startIndex + i + 1
            items.add(
                FoundRepoDto(
                    author = sampleAuthors.random(),
                    name = "Repo_${conditions.query.replace(" ", "_")}_${uniqueId}",
                    description = sampleDescriptions.random() + " (Page $page, Item ${i + 1})",
                    language = sampleLanguages.random(),
                    stars = Random.nextInt(0, 5000)
                )
            )
        }

        return SearchResponseDto(
            totalCount = totalCount,
            items = items
        )
    }
}