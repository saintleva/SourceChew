package com.github.saintleva.sourcechew.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.about_application_text
import sourcechew.composeapp.generated.resources.github_logo_1024px
import sourcechew.composeapp.generated.resources.logo


@Composable
private fun AboutApplicationContent() {
    Image(
        painter = painterResource(Res.drawable.github_logo_1024px),
        contentDescription = stringResource(Res.string.logo)
    )
    Spacer(modifier = Modifier.width(10.dp))
    Text(
        text = stringResource(Res.string.about_application_text)
    )
}

@Composable
fun AboutApplicationScreen(modifier: Modifier) {
    val scrollState = rememberScrollState() //TODO: Is it right?

    BoxWithConstraints {
        if (maxWidth > 600.dp) {
            Row(
                modifier = modifier
                    .padding(10.dp)
                    .verticalScroll(scrollState),
                verticalAlignment = Alignment.CenterVertically
            ) { AboutApplicationContent() }
        } else {
            Column(
                modifier = modifier
                    .padding(10.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { AboutApplicationContent() }
        }
    }
}