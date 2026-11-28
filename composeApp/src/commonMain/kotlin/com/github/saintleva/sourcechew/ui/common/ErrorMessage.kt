package com.github.saintleva.sourcechew.ui.common

import androidx.compose.runtime.Composable
import com.github.saintleva.sourcechew.domain.result.CommonApiError
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.DownloadBranchError
import com.github.saintleva.sourcechew.domain.result.InvalidTokenException
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.PagingBranchesException
import com.github.saintleva.sourcechew.domain.result.PagingMembersException
import com.github.saintleva.sourcechew.domain.result.PagingOwnerReposException
import com.github.saintleva.sourcechew.domain.result.PagingSearchException
import com.github.saintleva.sourcechew.domain.result.PagingUserOrgsException
import com.github.saintleva.sourcechew.domain.result.SearchError
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.error_common_access
import sourcechew.composeapp.generated.resources.error_deserialization
import sourcechew.composeapp.generated.resources.error_invalid_token
import sourcechew.composeapp.generated.resources.error_network
import sourcechew.composeapp.generated.resources.error_not_found
import sourcechew.composeapp.generated.resources.error_rate_limit_exceeded
import sourcechew.composeapp.generated.resources.error_server
import sourcechew.composeapp.generated.resources.error_unauthorized
import sourcechew.composeapp.generated.resources.error_unknown_api
import sourcechew.composeapp.generated.resources.error_unknown_infrastructure
import sourcechew.composeapp.generated.resources.error_validation
import sourcechew.composeapp.generated.resources.unknown_error


@Composable
fun getErrorMessage(error: Throwable): String {
    return when (error) {
        is PagingSearchException -> renderSearchError(error.error)
        is PagingBranchesException -> renderDetailsError(error.error)
        is PagingMembersException -> renderDetailsError(error.error)
        is PagingUserOrgsException -> renderDetailsError(error.error)
        is PagingOwnerReposException -> renderDetailsError(error.error)
        is NetworkException -> stringResource(Res.string.error_network)
        is DeserializationException -> stringResource(Res.string.error_deserialization)
        is InvalidTokenException -> stringResource(Res.string.error_invalid_token)
        is UnknownInfrastructureException -> stringResource(Res.string.error_unknown_infrastructure)
        else -> stringResource(Res.string.unknown_error)
    }
}

@Composable
fun getErrorMessage(detailsError: DetailsError): String {
    return renderDetailsError(detailsError)
}

@Composable
fun getErrorMessage(downloadBranchError: DownloadBranchError): String {
    return when (downloadBranchError) {
        is DownloadBranchError.Details -> renderDetailsError(downloadBranchError.detailsError)
        is DownloadBranchError.Infrastructure -> getErrorMessage(downloadBranchError.cause)
        is DownloadBranchError.Unknown -> getErrorMessage(downloadBranchError.throwable)
    }
}

@Composable
fun getErrorMessage(searchError: SearchError): String {
    return renderSearchError(searchError)
}

@Composable
fun renderCommonApiError(commonApiError: CommonApiError): String = when (commonApiError) {
    is CommonApiError.CommonAccessError -> stringResource(Res.string.error_common_access)
    is CommonApiError.Unauthorized -> stringResource(Res.string.error_unauthorized)
    is CommonApiError.RateLimitExceeded -> stringResource(Res.string.error_rate_limit_exceeded)
    is CommonApiError.ServerError -> stringResource(Res.string.error_server)
    is DetailsError.ResourceNotFound -> stringResource(Res.string.error_not_found)
    is CommonApiError.BadRequest -> {
        commonApiError.message ?: stringResource(Res.string.error_validation, "Bad Request (400)")
    }
    is CommonApiError.Conflict -> {
        commonApiError.message ?: stringResource(Res.string.error_validation, "Conflict (409)")
    }
    is CommonApiError.UnprocessableEntity -> {
        commonApiError.message ?: stringResource(Res.string.error_validation, "Unprocessable Entity (422)")
    }
    is CommonApiError.UnknownApiError -> {
        commonApiError.message ?: stringResource(
            Res.string.error_unknown_api, commonApiError.statusCode
        )
    }
}

@Composable
fun renderSearchError(searchError: SearchError): String = when (searchError) {
    is SearchError.Validation -> stringResource(Res.string.error_validation, searchError.reason)
    is SearchError.SearchNotFound -> stringResource(Res.string.error_not_found)
    is CommonApiError -> renderCommonApiError(searchError)
}

@Composable
fun renderDetailsError(detailsError: DetailsError): String = when (detailsError) {
    is DetailsError.ResourceNotFound -> stringResource(Res.string.error_not_found)
    is CommonApiError -> renderCommonApiError(detailsError)
}
