package fr.stein.maxbooker.domain.repository.sncf

import fr.stein.maxbooker.data.exception.SncfApiException
import retrofit2.Response

interface SncfApiExecutor {
    @Throws(SncfApiException::class)
    suspend fun <T : Any> execute(call: suspend () -> Response<T>): T?
    suspend fun <T : Any> executeNonNullBody(call: suspend () -> Response<T>): T
}
