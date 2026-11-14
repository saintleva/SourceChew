/*
 * Copyright (C) Anton Liaukevich 2021-2022 <leva.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.saintleva.sourcechew.ui.screens.found

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.saintleva.sourcechew.domain.models.FoundBase
import com.github.saintleva.sourcechew.domain.pagination.SearchMetadata
import com.github.saintleva.sourcechew.ui.common.PaginatedList
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.found_items

import com.github.saintleva.sourcechew.ui.screens.PaginatedViewModel

@Composable
fun <FoundItem: FoundBase> FoundScreen(
    modifier: Modifier = Modifier,
    viewModel: PaginatedViewModel<FoundItem>,
    itemsTitle: String? = null,
    showMetadata: Boolean = true,
    itemContent: @Composable (FoundItem) -> Unit
) {
    PaginatedList(
        viewModel = viewModel,
        modifier = modifier,
        key = { it.id }
    ) { item, globalIndex, _, _, page ->
        if (globalIndex == 0 && showMetadata && itemsTitle != null) {
            val meta = page.metadata as? SearchMetadata
            meta?.let { MetadataHeader(it, itemsTitle = itemsTitle) }
        }
        itemContent(item)
    }
}

@Composable
private fun MetadataHeader(
    metadata: SearchMetadata,
    itemsTitle: String
) {
    Text(
        text = "$itemsTitle: ${metadata.totalCount}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}
