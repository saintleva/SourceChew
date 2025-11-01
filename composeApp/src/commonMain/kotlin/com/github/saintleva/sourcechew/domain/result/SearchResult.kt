package com.github.saintleva.sourcechew.domain.result

import com.github.saintleva.sourcechew.domain.models.FoundRepo


/**
 * A type alias for the standard Kotlin Result class, used to return the outcome of a domain operation.
 * On success, it contains data of type [T].
 * On failure, it contains a [DomainError] object representing an expected, controllable business error.
 *
 * This approach makes the function signature explicit about the types of business errors it can return.
 *
 * Unexpected, uncontrollable failures (e.g., no network, parsing errors) should be thrown as an [AppException].
 */
typealias SearchResult<T> = Result<T, SearchError>

/**
 * A sealed class representing all expected business errors that an operation (like an API call) can return.
 * This class MUST NOT contain localized, user-facing messages. It should only carry structured data
 * about the error, which the presentation layer can use to look up the correct localized string.
 */
sealed class SearchError {
    /**
     * Represents a request validation error (e.g., HTTP 422).
     * The server rejected the request because the parameters were incorrect.
     * @param reason The non-localized reason from the server, which can be useful for logging or debugging.
     */
    data class Validation(val reason: String) : SearchError()

    /**
     * Represents an error related to authentication or exceeding API rate limits (e.g., HTTP 401, 403).
     */
    data object ApiLimitOrAuth : SearchError()

    /**
     * Represents a "resource not found" error (e.g., HTTP 404).
     */
    data object NotFound : SearchError()

    /**
     * Represents a server-side issue on the API's end (e.g., HTTP 5xx).
     * The server failed to process a valid request.
     */
    data object ServerError : SearchError()

    /**
     * Represents an unknown or unhandled API error.
     * This is a fallback for any non-successful status codes that are not handled specifically.
     * @param statusCode The HTTP status code of the response.
     */
    data class UnknownApiError(val statusCode: Int) : SearchError()
}

typealias RepoSearchResult = SearchResult<List<FoundRepo>>