package com.github.saintleva.sourcechew.domain.result

/**
 * Marker interface for all expected domain/business errors returned wrapped in [Result.Failure].
 * Unexpected or uncontrollable failures (e.g. no network, parsing errors) should be thrown as [AppException].
 */
sealed interface AppError

/**
 * Domain errors specific to search operations.
 */
sealed interface SearchError : AppError {
    /**
     * Represents a request validation error (e.g. HTTP 422).
     * @param reason The non-localized reason from the server.
     */
    data class Validation(val reason: String) : SearchError
}

/**
 * Domain errors specific to details operations (repository or owner details).
 */
sealed interface DetailsError : AppError

/**
 * Common REST API errors that can occur across any GitHub API endpoint.
 * These implement both [SearchError] and [DetailsError].
 */
sealed interface CommonApiError : SearchError, DetailsError {
    /**
     * Represents a "resource not found" error (HTTP 404).
     */
    data object NotFound : CommonApiError

    /**
     * Represents an unauthorized access error (HTTP 401).
     */
    data object Unauthorized : CommonApiError

    /**
     * Represents rate limit exceeded (HTTP 403 with rate limit or HTTP 429).
     */
    data object RateLimitExceeded : CommonApiError

    /**
     * Represents a common access/forbidden error (HTTP 403).
     */
    data object CommonAccessError : CommonApiError

    /**
     * Represents a server-side issue on the API end (HTTP 5xx).
     */
    data object ServerError : CommonApiError

    /**
     * Represents an unknown or unhandled API error.
     * @param statusCode The HTTP status code of the response.
     */
    data class UnknownApiError(val statusCode: Int) : CommonApiError
}
