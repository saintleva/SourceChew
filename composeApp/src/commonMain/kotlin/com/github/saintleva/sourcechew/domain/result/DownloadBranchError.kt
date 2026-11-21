package com.github.saintleva.sourcechew.domain.result

sealed interface DownloadBranchError : AppError {
    data class Details(val detailsError: DetailsError) : DownloadBranchError
    data class Infrastructure(val cause: AppException) : DownloadBranchError
    data class Unknown(val throwable: Throwable) : DownloadBranchError
}
