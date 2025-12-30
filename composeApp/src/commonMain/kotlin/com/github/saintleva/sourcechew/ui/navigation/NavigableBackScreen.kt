package com.github.saintleva.sourcechew.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.go_back


@Composable
fun BackIcon(onBackClick: () -> Unit) {
    IconButton(onClick = onBackClick) {
        Icon(
            Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = stringResource(Res.string.go_back)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigableBackScreen(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                //modifier = appBarModifier, //TODO: Remove it
                navigationIcon = {
                    BackIcon(onBackClick)
                }
            )
        }
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}
