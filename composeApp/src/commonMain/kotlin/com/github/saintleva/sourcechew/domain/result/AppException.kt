package com.github.saintleva.sourcechew.domain.result


/** * The base class for all exceptions related to the application's infrastructure (network, serialization, etc.).
 * It MUST NOT contain localized messages.
 */
open class AppException(cause: Throwable? = null) : RuntimeException(cause)

/**
 * An exception indicating a network-related problem (e.g., no connection, timeout, DNS failure).
 */
class NetworkException(cause: Throwable) : AppException(cause)

/**
 * An exception indicating a failure to parse (deserialize) the server's response.
 * This usually means a mismatch between the DTO and the actual JSON structure.
 */
class DeserializationException(cause: Throwable) : AppException(cause)

/**
 * An exception for any other unexpected infrastructure failures.
 */
class UnknownInfrastructureException(cause: Throwable) : AppException(cause)

class PagingSearchException(val error: SearchError): AppException(null)