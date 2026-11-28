package com.github.saintleva.sourcechew.domain.result

/**
 * Marker interface for all expected domain/business errors returned wrapped in [Result.Failure].
 * Unexpected or uncontrollable failures (e.g. no network, parsing errors) should be thrown as [AppException].
 */
sealed interface AppError

/**
 * Common REST API errors that can occur across any GitHub API endpoint.
 * These implement both [SearchError] and [DetailsError].
 */
sealed interface CommonApiError : SearchError, DetailsError {
    /**
     * Represents an unauthorized access error (HTTP 401).
     */
    data object Unauthorized : CommonApiError, SearchError, DetailsError

    /**
     * Represents rate limit exceeded (HTTP 403 with rate limit or HTTP 429).
     */
    data object RateLimitExceeded : CommonApiError, SearchError, DetailsError

    /**
     * Represents a common access/forbidden error (HTTP 403).
     */
    data object CommonAccessError : CommonApiError, SearchError, DetailsError

    /**
     * Represents a bad request error (HTTP 400).
     * @param message The optional error message from the server response body.
     */
    data class BadRequest(val message: String? = null) : CommonApiError, SearchError, DetailsError

    /**
     * Represents a request conflict error (HTTP 409).
     * @param message The optional error message from the server response body.
     */
    data class Conflict(val message: String? = null) : CommonApiError, SearchError, DetailsError

    /**
     * Represents an unprocessable entity error (HTTP 422).
     * @param message The optional error message from the server response body.
     */
    data class UnprocessableEntity(val message: String? = null) : CommonApiError, SearchError, DetailsError

    /**
     * Represents a server-side issue on the API end (HTTP 5xx).
     */
    data object ServerError : CommonApiError, SearchError, DetailsError

    /**
     * Represents an unknown or unhandled API error.
     * @param statusCode The HTTP status code of the response.
     * @param message The optional error message from the server response body.
     */
    data class UnknownApiError(
        val statusCode: Int,
        val message: String? = null
    ) : CommonApiError, SearchError, DetailsError
}

/**
 * Domain errors specific to search operations.
 */
sealed interface SearchError : AppError {
    /**
     * Represents a search request returning HTTP 404.
     */
    data object SearchNotFound : SearchError

    /**
     * Represents a request validation error (e.g. HTTP 422).
     * @param reason The non-localized reason from the server.
     */
    data class Validation(val reason: String) : SearchError
}

/**
 * Domain errors specific to details operations (repository or owner details).
 */
sealed interface DetailsError : AppError {
    /**
     * Represents a requested resource (repository or owner/user) not found error (HTTP 404).
     */
    data object ResourceNotFound : DetailsError, CommonApiError, SearchError
}