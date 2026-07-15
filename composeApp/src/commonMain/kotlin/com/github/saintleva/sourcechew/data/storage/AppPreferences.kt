package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.OwnerSearchConditions
import com.github.saintleva.sourcechew.domain.models.RepoSearchConditions
import com.github.saintleva.sourcechew.domain.repository.ConfigLens
import kotlinx.serialization.Serializable


@Serializable
data class AppPreferences(
    val appSettings: AppSettings = AppSettings.default,
    val repoSearchConditions: RepoSearchConditions = RepoSearchConditions.default,
    val ownerSearchConditions: OwnerSearchConditions = OwnerSearchConditions.default,
) {
    companion object {
        val AppSettingsLens = object : ConfigLens<AppPreferences, AppSettings> {
            override fun get(whole: AppPreferences) = whole.appSettings
            override fun set(whole: AppPreferences, part: AppSettings) = whole.copy(appSettings = part)
        }

        val RepoSearchLens = object : ConfigLens<AppPreferences, RepoSearchConditions> {
            override fun get(whole: AppPreferences) = whole.repoSearchConditions
            override fun set(whole: AppPreferences, part: RepoSearchConditions) = whole.copy(repoSearchConditions = part)
        }

        val OwnerSearchLens = object : ConfigLens<AppPreferences, OwnerSearchConditions> {
            override fun get(whole: AppPreferences) = whole.ownerSearchConditions
            override fun set(whole: AppPreferences, part: OwnerSearchConditions) = whole.copy(ownerSearchConditions = part)
        }
    }
}