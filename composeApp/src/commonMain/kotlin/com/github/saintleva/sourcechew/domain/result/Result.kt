package com.github.saintleva.sourcechew.domain.result


/**
 * A generic, sealed class that represents the outcome of an operation that can either succeed or fail.
 * This is a custom implementation inspired by functional programming concepts.
 *
 * @param T The type of the success value.
 * @param E The type of the error value.
 */
sealed class Result<out T, out E> {
    /**
     * Represents a successful outcome.
     * @property value The successfully computed value.
     */
    data class Success<out T>(val value: T) : Result<T, Nothing>()

    /**
     * Represents a failed outcome.
     * @property error The error object containing details about the failure.
     */
    data class Failure<out E>(val error: E) : Result<Nothing, E>()
}