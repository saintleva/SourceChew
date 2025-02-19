package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.domain.models.Forge
import com.github.saintleva.sourcechew.domain.models.SearchConditions
import com.github.saintleva.sourcechew.domain.models.TypeOptions
import io.github.aakira.napier.Napier
import kotlin.random.Random


object ConfigManagerStub : ConfigManager {

    override suspend fun savePreviousConditions(value: SearchConditions) {
        Napier.d(tag = "ConfigManagerStub") {
            """
                |previousConditions:
                |    forgeOptions:
                |        ${value.forgeOptions}
                |    typeOptions:
                |        repo: ${value.typeOptions.repo}
                |        user: ${value.typeOptions.user}
                |        group: ${value.typeOptions.group}
                |    text: "${value.text}"
            """.trimMargin()
        }
    }

    override suspend fun loadPreviousConditions(): SearchConditions {
        val random = Random.Default
        return SearchConditions(
            forgeOptions = Forge.list.associateWith { random.nextBoolean() },
            typeOptions = TypeOptions(
                repo = random.nextBoolean(),
                user = random.nextBoolean(),
                group = random.nextBoolean()
            ),
            text = "Some text"
        )
    }

    override suspend fun saveUsePreviousSearch(value: Boolean) {
        Napier.d(tag = "ConfigManagerStub") { "UsePreviousSearch: $value" }
    }

    override suspend fun loadUsePreviousSearch(): Boolean {
        val random = Random.Default
        return random.nextBoolean()
    }
}