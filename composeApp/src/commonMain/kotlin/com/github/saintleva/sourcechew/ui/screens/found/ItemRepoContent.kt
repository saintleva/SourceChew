package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.FoundRepo


@Composable
fun ItemRepoContent(repo: FoundRepo) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Name: ${repo.name}")
            Text("Full name: ${repo.fullName}")
            Text("Owner: ${repo.owner.login}")
            Text("Description: ${repo.description ?: ""}")
            Text("Language: ${repo.language ?: ""}")
            Text("Stars: ${repo.stars}")
        }
    }
}