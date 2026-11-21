package com.github.saintleva.sourcechew.ui.screens.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.saintleva.sourcechew.domain.models.DownloadBranchStatus
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.ui.common.getErrorMessage
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.dialog_cancel
import sourcechew.composeapp.generated.resources.dialog_ok
import sourcechew.composeapp.generated.resources.download_branch_downloading
import sourcechew.composeapp.generated.resources.download_branch_extracting
import sourcechew.composeapp.generated.resources.download_branch_success
import sourcechew.composeapp.generated.resources.download_branch_title
import sourcechew.composeapp.generated.resources.retry_button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadBranchBottomSheet(
    repoRef: RepoRef,
    branch: String,
    viewModel: DownloadBranchViewModel,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status by viewModel.downloadStatus.collectAsStateWithLifecycle()

    LaunchedEffect(repoRef, branch) {
        viewModel.startDownload(repoRef, branch)
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (status is DownloadBranchStatus.Downloading) {
                viewModel.cancelDownload()
            }
            onDismissRequest()
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.download_branch_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${repoRef.owner}/${repoRef.name}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = branch,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            when (val currentStatus = status) {
                is DownloadBranchStatus.Idle, is DownloadBranchStatus.Downloading -> {
                    val downloadingState = currentStatus as? DownloadBranchStatus.Downloading
                    val progress = downloadingState?.progress

                    Text(
                        text = stringResource(Res.string.download_branch_downloading),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (progress != null) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    if (downloadingState != null) {
                        val downloadedMb = downloadingState.bytesDownloaded / (1024f * 1024f)
                        val sizeText = if (downloadingState.totalBytes != null && downloadingState.totalBytes > 0L) {
                            val totalMb = downloadingState.totalBytes / (1024f * 1024f)
                            val percentage = (progress!! * 100).toInt()
                            "${formatMb(downloadedMb)} / ${formatMb(totalMb)} MB ($percentage%)"
                        } else {
                            "${formatMb(downloadedMb)} MB"
                        }
                        Text(
                            text = sizeText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.cancelDownload()
                            onDismissRequest()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.dialog_cancel))
                    }
                }

                is DownloadBranchStatus.Extracting -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Text(
                        text = stringResource(Res.string.download_branch_extracting),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                is DownloadBranchStatus.Success -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(Res.string.download_branch_success),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentStatus.targetDirectory,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.dialog_ok))
                    }
                }

                is DownloadBranchStatus.Failed -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = getErrorMessage(currentStatus.error),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(Res.string.dialog_cancel))
                        }
                        Button(
                            onClick = viewModel::retry,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(Res.string.retry_button))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatMb(value: Float): String {
    val rounded = (value * 100).toInt() / 100f
    return rounded.toString()
}
