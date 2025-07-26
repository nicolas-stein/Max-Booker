package fr.stein.maxbooker.data.exception

open class SncfRepositoryException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)

    class NetworkException(cause: Throwable) :
        SncfRepositoryException("Network error occurred: ${cause.message}", cause)
    class ParsingException(cause: Throwable) :
        SncfRepositoryException("Failed to parse response: ${cause.message}", cause)
    class ApiErrorException(code: Int, errorBody: String?) :
        SncfRepositoryException("API error: HTTP $code - ${errorBody ?: "No error body"}")
    class EmptyBodyException : SncfRepositoryException("Response body was null")
}
