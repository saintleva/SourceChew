package com.github.saintleva.sourcechew.ui.common

import androidx.compose.runtime.Composable
import com.github.saintleva.sourcechew.domain.result.DeserializationException
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.PagingSearchException
import com.github.saintleva.sourcechew.domain.result.SearchError
import com.github.saintleva.sourcechew.domain.result.UnknownInfrastructureException
import org.jetbrains.compose.resources.stringResource
import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.error_api_limit
import sourcechew.composeapp.generated.resources.error_deserialization
import sourcechew.composeapp.generated.resources.error_network
import sourcechew.composeapp.generated.resources.error_not_found
import sourcechew.composeapp.generated.resources.error_server
import sourcechew.composeapp.generated.resources.error_unknown_api
import sourcechew.composeapp.generated.resources.error_unknown_infrastructure
import sourcechew.composeapp.generated.resources.error_validation
import sourcechew.composeapp.generated.resources.unknown_error


@Composable
fun getErrorMessage(error: Throwable): String {
    // This 'when' block inspects the exception type to provide a specific, user-friendly, and localized error message.
    return when (error) {
        // Unpack our custom PagingDomainException to access the underlying business error.
        is PagingSearchException ->
            when (val searchError = error.error) {
                is SearchError.ApiLimitOrAuth -> stringResource(Res.string.error_api_limit)
                is SearchError.ServerError -> stringResource(Res.string.error_server)
                is SearchError.Validation -> stringResource(Res.string.error_validation,
                    searchError.reason)
                is SearchError.UnknownApiError -> stringResource(Res.string.error_unknown_api,
                    searchError.statusCode)
                is SearchError.NotFound -> stringResource(Res.string.error_not_found)
            }
        // Handle infrastructure errors.
        is NetworkException -> stringResource(Res.string.error_network)
        is DeserializationException -> stringResource(Res.string.error_deserialization)
        is UnknownInfrastructureException -> stringResource(Res.string.error_unknown_infrastructure)
        // A final fallback for any other unexpected exceptions.
        else -> stringResource(Res.string.unknown_error)
    }
}