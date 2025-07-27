package fr.stein.maxbooker.data.exception

open class SncfApiRepositoryException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)

    class NetworkException(cause: Throwable) :
        SncfApiRepositoryException("Network error occurred: ${cause.message}", cause)
    class ParsingException(cause: Throwable) :
        SncfApiRepositoryException("Failed to parse response: ${cause.message}", cause)
    class ApiErrorException(val code: Int, errorBody: String?) :
        SncfApiRepositoryException("API error: HTTP $code - ${errorBody ?: "No error body"}")
    class EmptyBodyException : SncfApiRepositoryException("Response body was null")

    class InvalidAuthentication : SncfApiRepositoryException("Invalid credentials provided to API")

    class AuthenticatedRequired : SncfApiRepositoryException("API requires authentication")
}
