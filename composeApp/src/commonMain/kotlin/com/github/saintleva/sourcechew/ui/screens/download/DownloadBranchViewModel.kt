package com.github.saintleva.sourcechew.ui.screens.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.saintleva.sourcechew.domain.models.BranchRef
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

    private val _activeTarget = MutableStateFlow<BranchRef?>(null)
    val activeTarget: StateFlow<BranchRef?> = _activeTarget.asStateFlow()

    private var downloadJob: Job? = null

    fun startDownload(branchRef: BranchRef) {
        if (_activeTarget.value == branchRef &&
            (_downloadStatus.value is DownloadBranchStatus.Downloading || _downloadStatus.value is DownloadBranchStatus.Extracting)
        ) {
            return
        }

        downloadJob?.cancel()
        _activeTarget.value = branchRef

        downloadJob = viewModelScope.launch {
            downloadBranchUseCase(branchRef).collectLatest { status ->
                _downloadStatus.value = status
            }
        }
    }

    fun startDownload(repoRef: RepoRef, branch: String) {
        startDownload(BranchRef(repoRef, branch))
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob = null
        _downloadStatus.value = DownloadBranchStatus.Idle
        _activeTarget.value = null
    }

    fun retry() {
        val target = _activeTarget.value ?: return
        startDownload(target)
    }

    fun reset() {
        cancelDownload()
    }
}
