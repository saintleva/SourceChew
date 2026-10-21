package com.github.saintleva.sourcechew.ui.common

import androidx.compose.runtime.Composable
import com.github.saintleva.sourcechew.domain.result.CommonApiError
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.InvalidTokenException
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.PagingSearchException
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
fun getErrorMessage(searchError: SearchError): String {
    return renderSearchError(searchError)
}

@Composable
fun renderCommonApiError(commonApiError: CommonApiError): String = when (commonApiError) {
    is CommonApiError.CommonAccessError -> stringResource(Res.string.error_common_access)
    is CommonApiError.Unauthorized -> stringResource(Res.string.error_unauthorized)
    is CommonApiError.RateLimitExceeded -> stringResource(Res.string.error_rate_limit_exceeded)
    is CommonApiError.ServerError -> stringResource(Res.string.error_server)
    is CommonApiError.UnknownApiError -> stringResource(Res.string.error_unknown_api, commonApiError.statusCode)
}

@Composable
fun renderSearchError(searchError: SearchError): String = when (searchError) {
    is SearchError.Validation -> stringResource(Res.string.error_validation, searchError.reason)
    is CommonApiError -> renderCommonApiError(searchError)
}

@Composable
fun renderDetailsError(detailsError: DetailsError): String = when (detailsError) {
    is DetailsError.ResourceNotFound -> stringResource(Res.string.error_not_found)
    is CommonApiError -> renderCommonApiError(detailsError)
}
