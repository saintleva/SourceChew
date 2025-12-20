package com.github.saintleva.sourcechew.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.access_token
import sourcechew.composeapp.generated.resources.enter_api_token
import sourcechew.composeapp.generated.resources.save
import sourcechew.composeapp.generated.resources.remove
import sourcechew.composeapp.generated.resources.you_are_authorized
import sourcechew.composeapp.generated.resources.you_are_not_authorized


@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val isAuthorized = viewModel.isAuthorized.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
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
        OutlinedTextField(
            value = viewModel.token.value,
            onValueChange = viewModel::onTokenChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.enter_api_token)) },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = viewModel::onTokenSave,
            enabled = viewModel.canSave(),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(stringResource(Res.string.save))
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
