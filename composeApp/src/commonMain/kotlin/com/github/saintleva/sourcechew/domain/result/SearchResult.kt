package com.github.saintleva.sourcechew.domain.result

/**
 * A type alias for the standard Kotlin Result class, used to return the outcome of a search operation.
 * On success, it contains data of type [T].
 * On failure, it contains a [SearchError] object representing an expected, controllable business error.
 *
 * Unexpected, uncontrollable failures (e.g., no network, parsing errors) should be thrown as an [AppException].
 */
typealias SearchResult<T> = Result<T, SearchError>

/**
 * A type alias for the Result class, used to return the outcome of a details operation.
 */
typealias DetailsResult<T> = Result<T, DetailsError>
