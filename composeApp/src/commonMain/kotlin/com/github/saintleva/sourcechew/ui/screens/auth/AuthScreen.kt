package com.github.saintleva.sourcechew.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.access_token
import sourcechew.composeapp.generated.resources.change
import sourcechew.composeapp.generated.resources.copy
import sourcechew.composeapp.generated.resources.enter_api_token
import sourcechew.composeapp.generated.resources.paste
import sourcechew.composeapp.generated.resources.remove
import sourcechew.composeapp.generated.resources.save
import sourcechew.composeapp.generated.resources.you_are_authorized
import sourcechew.composeapp.generated.resources.you_are_not_authorized


@Composable
fun AuthScreen(
    modifier: Modifier,
    viewModel: AuthViewModel
) {
    val isAuthorized = viewModel.isAuthorized.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.access_token),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = if (isAuthorized.value) {
                stringResource(Res.string.you_are_authorized)
            } else {
                stringResource(Res.string.you_are_not_authorized)
            },
            style = MaterialTheme.typography.headlineSmall,
            color = if (isAuthorized.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = viewModel::onTokenPaste,
                contentPadding = PaddingValues(horizontal = 8.dp),
                enabled = viewModel.isClipboardNotEmpty()
            ) {
                Icon(Icons.Default.ContentPaste, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(Res.string.paste), style = MaterialTheme.typography.labelMedium)
            }
            TextButton(
                onClick = viewModel::onTokenCopy,
                contentPadding = PaddingValues(horizontal = 8.dp),
                enabled = viewModel.token.value.isNotEmpty()
            ) {
                Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(Res.string.copy), style = MaterialTheme.typography.labelMedium)
            }
        }
        OutlinedTextField(
            value = viewModel.token.value,
            onValueChange = viewModel::onTokenChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.enter_api_token)) },
            singleLine = true,
            trailingIcon = {
                if (viewModel.token.value.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onTokenChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Napier.d(tag = "AuthScreen") { "Token: ${viewModel.token.value}" }
        Button(
            onClick = viewModel::onTokenSave,
            enabled = viewModel.canSaveState.value,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                stringResource(
                    if (isAuthorized.value) Res.string.change else Res.string.save
                )
            )
        }
        if (isAuthorized.value) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = viewModel::onTokenClear,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(Res.string.remove))
            }
        }
    }
}
