package fr.stein.maxbooker.data.exception

import java.io.IOException

sealed class SncfApiException(message: String? = null, cause: Throwable? = null) :
    IOException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)

    class NetworkException(cause: Throwable) :
        SncfApiException("Network error occurred: ${cause.message}", cause)
    class ParsingException(cause: Throwable) :
        SncfApiException("Failed to parse response: ${cause.message}", cause)
    class ApiErrorException(val code: Int, errorBody: String?) :
        SncfApiException("API error: HTTP $code - ${errorBody ?: "No error body"}")
    class EmptyBodyException : SncfApiException("Response body was null")

    class InvalidAuthentication : SncfApiException("Invalid credentials provided to API")

    class AuthenticatedRequired : SncfApiException("API requires authentication")

    class UnexpectedException(cause: Throwable) :
        SncfApiException("Unexpected exception : ${cause.message}", cause)
}
