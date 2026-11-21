package com.github.saintleva.sourcechew.ui.screens.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.DownloadBranchStatus
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.usecase.DownloadBranchUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DownloadBranchViewModel(
    private val downloadBranchUseCase: DownloadBranchUseCase
) : ViewModel() {

    private val _downloadStatus = MutableStateFlow<DownloadBranchStatus>(DownloadBranchStatus.Idle)
    val downloadStatus: StateFlow<DownloadBranchStatus> = _downloadStatus.asStateFlow()

    private val _activeTarget = MutableStateFlow<Pair<RepoRef, String>?>(null)
    val activeTarget: StateFlow<Pair<RepoRef, String>?> = _activeTarget.asStateFlow()

    private var downloadJob: Job? = null

    fun startDownload(repoRef: RepoRef, branch: String) {
        if (_activeTarget.value == Pair(repoRef, branch) &&
            (_downloadStatus.value is DownloadBranchStatus.Downloading || _downloadStatus.value is DownloadBranchStatus.Extracting)
        ) {
            return
        }

        downloadJob?.cancel()
        _activeTarget.value = Pair(repoRef, branch)

        downloadJob = viewModelScope.launch {
            downloadBranchUseCase(repoRef, branch).collectLatest { status ->
                _downloadStatus.value = status
            }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob = null
        _downloadStatus.value = DownloadBranchStatus.Idle
        _activeTarget.value = null
    }

    fun retry() {
        val target = _activeTarget.value ?: return
        startDownload(target.first, target.second)
    }

    fun reset() {
        cancelDownload()
    }
}
